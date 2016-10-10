package com.smartbuilders.smartsales.salesforcesystem;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.data.BusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerce.model.BusinessPartner;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.utils.ApplicationUtilities;

/**
 * Created by Jesus Sarco, 01.10.2016
 */
public class SalesForceSystemMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SalesForceSystemMainActivityFragment.Callback  {

    private User mUser;
    private boolean doubleBackToExitPressedOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_force_system_main);

        mUser = Utils.getCurrentUser(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.setCustomToolbarTitle(this, toolbar, false);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view); //must call super
                Utils.loadNavigationViewBadge(getApplicationContext(), mUser,
                        (NavigationView) findViewById(R.id.nav_view));
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if(navigationView!=null && mUser !=null){
            navigationView.inflateMenu(R.menu.sales_force_system_drawer_menu);
            navigationView.setNavigationItemSelectedListener(this);
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name))
                    .setText(getString(R.string.welcome_user, mUser.getUserName()));
        }

        try {
            BusinessPartner businessPartner = (new BusinessPartnerDB(this, mUser))
                    .getBusinessPartnerById(Utils.getAppCurrentBusinessPartnerId(this, mUser));
            if (businessPartner!=null) {
                ((TextView) findViewById(R.id.business_partner_name_textView))
                        .setText(getString(R.string.business_partner_name_detail, businessPartner.getName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ApplicationUtilities.checkAppVersion(this, mUser);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer!=null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (isTaskRoot()) {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, R.string.click_back_again_to_exit, Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            } else {
                super.onBackPressed();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
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
    public void reload() {
        try {
            BusinessPartner businessPartner = (new BusinessPartnerDB(this, mUser))
                    .getBusinessPartnerById(Utils.getAppCurrentBusinessPartnerId(this, mUser));
            if (businessPartner!=null) {
                ((TextView) findViewById(R.id.business_partner_name_textView))
                        .setText(getString(R.string.business_partner_name_detail, businessPartner.getName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

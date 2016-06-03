package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

/**
 * Jesus Sarco, 03.06.2016
 */
public class BusinessPartnersActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    public static final String KEY_CURRENT_USER = "KEY_CURRENT_USER";
    private static final String STATE_CURRENT_USER = "state_current_user";
    private static final String STATE_LISTVIEW_INDEX = "STATE_LISTVIEW_INDEX";
    private static final String STATE_LISTVIEW_TOP = "STATE_LISTVIEW_TOP";

    private User mCurrentUser;
    // save index and top position
    int mListViewIndex;
    int mListViewTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_partners);

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mCurrentUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
            if(savedInstanceState.containsKey(STATE_LISTVIEW_INDEX)){
                mListViewIndex = savedInstanceState.getInt(STATE_LISTVIEW_INDEX);
            }
            if(savedInstanceState.containsKey(STATE_LISTVIEW_TOP)){
                mListViewTop = savedInstanceState.getInt(STATE_LISTVIEW_TOP);
            }
        }

        if(getIntent()!=null && getIntent().getExtras()!=null){
            if(getIntent().getExtras().containsKey(KEY_CURRENT_USER)){
                mCurrentUser = getIntent().getExtras().getParcelable(KEY_CURRENT_USER);
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.setCustomToolbarTitle(this, toolbar, mCurrentUser, false);
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BusinessPartnersActivity.this, RegisterBusinessPartnerActivity.class)
                        .putExtra(RegisterBusinessPartnerActivity.KEY_CURRENT_USER, mCurrentUser));
            }
        });
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
        try {
            ListView listView = (ListView) findViewById(R.id.business_partnerts_list);
            if(listView!=null){
                outState.putInt(STATE_LISTVIEW_INDEX, listView.getFirstVisiblePosition());
                outState.putInt(STATE_LISTVIEW_TOP,
                        (listView.getChildAt(0) == null) ? 0 : (listView.getChildAt(0).getTop() - listView.getPaddingTop()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onSaveInstanceState(outState);
    }

}

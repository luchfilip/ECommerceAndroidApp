package com.smartbuilders.smartsales.ecommerceandroidapp;

import com.jasgcorp.ids.model.User;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.MainActivityRecyclerViewAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.MainPageSectionsDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;

import java.util.ArrayList;

/**
 * Created by Alberto on 22/3/2016.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String STATE_LISTVIEW_INDEX = "STATE_LISTVIEW_INDEX";
    private static final String STATE_LISTVIEW_TOP = "STATE_LISTVIEW_TOP";

    private User mCurrentUser;
    private ProgressDialog waitPlease;
    // save index and top position
    int mListViewIndex;
    int mListViewTop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(STATE_LISTVIEW_INDEX)){
                mListViewIndex = savedInstanceState.getInt(STATE_LISTVIEW_INDEX);
            }
            if(savedInstanceState.containsKey(STATE_LISTVIEW_TOP)){
                mListViewTop = savedInstanceState.getInt(STATE_LISTVIEW_TOP);
            }
        }

        mCurrentUser = Utils.getCurrentUser(this);

        if(getIntent().getData()!=null){//check if intent is not null
            Uri data = getIntent().getData();//set a variable for the Intent
            String scheme = data.getScheme();//get the scheme (http,https)
            String fullPath = data.getEncodedSchemeSpecificPart();//get the full path -scheme - fragments

            String combine = scheme+":"+fullPath; //combine to get a full URI
            String url = null;//declare variable to hold final URL
            if(combine!=null){//if combine variable is not empty then navigate to that full path
                //Log.d(TAG, "combine: "+combine);
                //url = combine;
            } else{//else open main page
                //Log.e(TAG, "combine is null");
                //url = "http://www.example.com";
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.setCustomToolbarTitle(this, toolbar, false);
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

        if(findViewById(R.id.search_bar_linear_layout)!=null){
            findViewById(R.id.search_by_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, FilterOptionsActivity.class));
                }
            });

            findViewById(R.id.search_product_editText).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, SearchResultsActivity.class));
                }
            });

            findViewById(R.id.image_search_bar_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, SearchResultsActivity.class));
                }
            });
        }

        waitPlease = ProgressDialog.show(this, getString(R.string.loading),
                getString(R.string.wait_please), true, false);
        new Thread() {
            @Override
            public void run() {
                try {
                    final MainPageSectionsDB mainPageSectionsDB = new MainPageSectionsDB(MainActivity.this, mCurrentUser);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadMainPage(mainPageSectionsDB.getActiveMainPageSections());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                waitPlease.dismiss();
                                waitPlease.cancel();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }.start();
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

    private void loadMainPage(ArrayList<Object> mainPageSections){
        ListView listView = (ListView) findViewById(R.id.main_categories_list);
        listView.setAdapter(new MainActivityRecyclerViewAdapter(this, mainPageSections, mCurrentUser));
        listView.setVisibility(View.VISIBLE);
        listView.setSelectionFromTop(mListViewIndex, mListViewTop);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        try {
            ListView listView = (ListView) findViewById(R.id.main_categories_list);
            outState.putInt(STATE_LISTVIEW_INDEX, listView.getFirstVisiblePosition());
            outState.putInt(STATE_LISTVIEW_TOP,
                    (listView.getChildAt(0) == null) ? 0 : (listView.getChildAt(0).getTop() - listView.getPaddingTop()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        if(waitPlease!=null && waitPlease.isShowing()){
            waitPlease.dismiss();
            waitPlease.cancel();
        }
        super.onDestroy();
    }
}

package com.smartbuilders.smartsales.ecommerceandroidapp;

import com.jasgcorp.ids.model.User;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jasgcorp.ids.syncadapter.SyncAdapter;
import com.jasgcorp.ids.utils.ApplicationUtilities;
import com.jasgcorp.ids.utils.NetworkConnectionUtilities;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.MainActivityRecyclerViewAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.MainPageDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;

import java.util.ArrayList;

/**
 * Created by Alberto on 22/3/2016.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String KEY_CURRENT_USER = "KEY_CURRENT_USER";
    private static final String STATE_CURRENT_USER = "state_current_user";
    private static final String STATE_LISTVIEW_INDEX = "STATE_LISTVIEW_INDEX";
    private static final String STATE_LISTVIEW_TOP = "STATE_LISTVIEW_TOP";

    private User mCurrentUser;
    private ProgressDialog waitPlease;
    // save index and top position
    int mListViewIndex;
    int mListViewTop;

    private BroadcastReceiver syncAdapterReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!=null && intent.getAction()!=null){
                Bundle extras = intent.getExtras();
                if(extras!=null){
                    if(extras.containsKey(SyncAdapter.USER_ID)
                            && extras.getString(SyncAdapter.USER_ID).equals(mCurrentUser.getUserId())){
                        if(intent.getAction().equals(SyncAdapter.AUTHENTICATOR_EXCEPTION)
                                || intent.getAction().equals(SyncAdapter.SYNCHRONIZATION_FINISHED)
                                || intent.getAction().equals(SyncAdapter.SYNCHRONIZATION_CANCELED)
                                || intent.getAction().equals(SyncAdapter.IO_EXCEPTION)
                                || intent.getAction().equals(SyncAdapter.GENERAL_EXCEPTION)){
                            if(intent.getAction().equals(SyncAdapter.SYNCHRONIZATION_FINISHED)){
                                loadData();
                            }else{
                                if (waitPlease!=null && waitPlease.isShowing()) {
                                    waitPlease.dismiss();
                                    waitPlease.cancel();
                                }
                                findViewById(R.id.error_loading_data_linearLayout).setVisibility(View.VISIBLE);
                                findViewById(R.id.main_categories_list).setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        try{
            IntentFilter intentFilter = new IntentFilter(SyncAdapter.SYNCHRONIZATION_STARTED);
            intentFilter.addAction(SyncAdapter.SYNCHRONIZATION_CANCELED);
            intentFilter.addAction(SyncAdapter.SYNCHRONIZATION_PROGRESS);
            intentFilter.addAction(SyncAdapter.SYNCHRONIZATION_FINISHED);
            intentFilter.addAction(SyncAdapter.AUTHENTICATOR_EXCEPTION);
            intentFilter.addAction(SyncAdapter.GENERAL_EXCEPTION);
            intentFilter.addAction(SyncAdapter.IO_EXCEPTION);
            intentFilter.addAction(SyncAdapter.OPERATION_CANCELED_EXCEPTION);
            intentFilter.addAction(SyncAdapter.XML_PULL_PARSE_EXCEPTION);
            getApplicationContext().registerReceiver(syncAdapterReceiver, intentFilter);
        }catch(Exception e){
            e.printStackTrace();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        checkInitialLoad();

        findViewById(R.id.exit_app_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.reTry_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.error_loading_data_linearLayout).setVisibility(View.GONE);
                findViewById(R.id.main_categories_list).setVisibility(View.GONE);
                checkInitialLoad();
            }
        });
    }


    /**
     * Verifica si tiene que
     */
    private void checkInitialLoad() {
        if (waitPlease!=null && waitPlease.isShowing()){
            waitPlease.dismiss();
            waitPlease.cancel();
        }

        if (mCurrentUser!=null && Utils.appRequireInitialLoad(this, mCurrentUser)) {
            Account[] accounts = AccountManager.get(this)
                    .getAccountsByType(getString(R.string.authenticator_acount_type));
            if(accounts!=null && accounts.length>0){
                if(NetworkConnectionUtilities.isOnline(this)
                        && (NetworkConnectionUtilities.isWifiConnected(this))||NetworkConnectionUtilities.isMobileConnected(this)) {
                    waitPlease = ProgressDialog.show(this, getString(R.string.loading_data),
                            getString(R.string.wait_please), true, false);
                    if(!ApplicationUtilities.isSyncActive(accounts[0], getString(R.string.sync_adapter_content_authority))){
                        Log.d(TAG, "!ApplicationUtilities.isSyncActive(accounts[0] ,getString(R.string.sync_adapter_content_authority))");
                        Bundle settingsBundle = new Bundle();
                        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
                        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                        ContentResolver.requestSync(accounts[0], getString(R.string.sync_adapter_content_authority), settingsBundle);
                    }else{
                        Log.d(TAG, "ApplicationUtilities.isSyncActive(accounts[0] ,getString(R.string.sync_adapter_content_authority))");
                    }
                } else {
                    //show network connection unavailable error.
                    Toast.makeText(this, R.string.network_connection_unavailable, Toast.LENGTH_SHORT).show();
                    //TODO: mostrar en pantalla error de conexion
                }
            }else{
                startActivity(new Intent(this, SplashScreen.class));
                finish();
            }
        } else if (mCurrentUser!=null) {
            loadData();
        } else {
            startActivity(new Intent(this, SplashScreen.class));
            finish();
        }
    }

    private void loadData(){
        if(waitPlease!=null && waitPlease.isShowing()){
            waitPlease.dismiss();
            waitPlease.cancel();
        }
        waitPlease = ProgressDialog.show(this, getString(R.string.loading),
                getString(R.string.wait_please), true, false);
        new Thread() {
            @Override
            public void run() {
                try {
                    final MainPageDB mainPageDB = new MainPageDB(MainActivity.this, mCurrentUser);
                    Utils.createImageFiles(MainActivity.this, mCurrentUser);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(findViewById(R.id.search_bar_linear_layout)!=null){
                                findViewById(R.id.search_by_button).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(MainActivity.this, FilterOptionsActivity.class)
                                                .putExtra(FilterOptionsActivity.KEY_CURRENT_USER, mCurrentUser));
                                    }
                                });

                                findViewById(R.id.search_product_editText).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(MainActivity.this, SearchResultsActivity.class)
                                                .putExtra(SearchResultsActivity.KEY_CURRENT_USER, mCurrentUser));
                                    }
                                });

                                findViewById(R.id.image_search_bar_layout).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(MainActivity.this, SearchResultsActivity.class)
                                                .putExtra(SearchResultsActivity.KEY_CURRENT_USER, mCurrentUser));
                                    }
                                });
                            }

                            loadMainPage(mainPageDB.getMainPageList());
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
        Utils.navigationItemSelectedBehave(item.getItemId(), this, mCurrentUser);
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
        outState.putParcelable(STATE_CURRENT_USER, mCurrentUser);
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
    protected void onStop() {
        super.onStop();
        try{
            unregisterReceiver(syncAdapterReceiver);
        }catch(Exception e){ }
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

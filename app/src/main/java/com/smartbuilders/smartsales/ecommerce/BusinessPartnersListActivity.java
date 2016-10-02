package com.smartbuilders.smartsales.ecommerce;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.model.UserProfile;
import com.smartbuilders.smartsales.ecommerce.adapters.BusinessPartnersListAdapter;
import com.smartbuilders.smartsales.ecommerce.data.BusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerce.data.UserBusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

/**
 * Jesus Sarco, 03.06.2016
 */
public class BusinessPartnersListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BusinessPartnersListFragment.Callback, RegisterBusinessPartnerFragment.Callback,
        DialogRegisterUserBusinessPartner.Callback {

    public static final String REGISTER_BUSINESS_PARTNER_FRAGMENT_TAG = "REGISTER_BUSINESS_PARTNER_FRAGMENT_TAG";

    private UserBusinessPartnerDB mUserBusinessPartnerDB;
    private BusinessPartnerDB mBusinessPartnerDB;
    private boolean mTwoPane;
    private ListView mListView;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_partners_list);

        mUser = Utils.getCurrentUser(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.setCustomToolbarTitle(this, toolbar, true);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        if(navigationView!=null && mUser!=null){
            if(BuildConfig.IS_SALES_FORCE_SYSTEM){
                navigationView.inflateMenu(R.menu.sales_force_system_drawer_menu);
            }else if(mUser.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID){
                navigationView.inflateMenu(R.menu.business_partner_drawer_menu);
            }else if(mUser.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID){
                navigationView.inflateMenu(R.menu.sales_man_drawer_menu);
            }
            navigationView.setNavigationItemSelectedListener(this);
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name))
                    .setText(getString(R.string.welcome_user, mUser.getUserName()));
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab!=null) {
            if(mUser!=null){
                if(mUser.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID){
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mTwoPane) {
                                showDialogCreateBusinessPartner(mUser);
                            } else {
                                startActivity(new Intent(BusinessPartnersListActivity.this,
                                        RegisterBusinessPartnerActivity.class));
                            }
                        }
                    });
                }else if(mUser.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID){
                    fab.setVisibility(View.GONE);
                }
            }
        }

        mTwoPane = findViewById(R.id.business_partner_detail_container)!=null;
        if(mUser!=null){
            if(mUser.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID){
                mUserBusinessPartnerDB = new UserBusinessPartnerDB(this, mUser);
            }else if(mUser.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID){
                mBusinessPartnerDB = new BusinessPartnerDB(this, mUser);
            }
        }

        mListView = (ListView) findViewById(R.id.business_partners_list);
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
        getMenuInflater().inflate(R.menu.menu_business_partners_list, menu);
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
    public void reloadBusinessPartnersList() {
        if (mListView!=null) {
            if (mUserBusinessPartnerDB != null) {
                if (mListView.getAdapter() != null) {
                    ((BusinessPartnersListAdapter) mListView.getAdapter()).setData(
                            mUserBusinessPartnerDB.getActiveUserBusinessPartners());
                } else {
                    mListView.setAdapter(new BusinessPartnersListAdapter(this,
                            mUserBusinessPartnerDB.getActiveUserBusinessPartners(), 0));
                }
            }else if(mBusinessPartnerDB != null){
                if (mListView.getAdapter() != null) {
                    ((BusinessPartnersListAdapter) mListView.getAdapter()).setData(
                            mBusinessPartnerDB.getActiveBusinessPartners());
                } else {
                    try{
                        mListView.setAdapter(new BusinessPartnersListAdapter(this,
                                mBusinessPartnerDB.getActiveBusinessPartners(),
                                Utils.getAppCurrentBusinessPartnerId(this, mUser)));
                    }catch (Exception e){
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
            if (mTwoPane && mListView.getAdapter()!=null) {
                mListView.performItemClick(mListView.getAdapter().getView(0, null, null), 0, 0);
            }
        }
    }

    private void showDialogCreateBusinessPartner(User user) {
        DialogRegisterUserBusinessPartner dialogRegisterUserBusinessPartner =
                DialogRegisterUserBusinessPartner.newInstance(user);
        dialogRegisterUserBusinessPartner.show(getSupportFragmentManager(),
                DialogRegisterUserBusinessPartner.class.getSimpleName());
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
        if (mTwoPane) {
            if (mListView != null && mListView.getAdapter()!=null && mListView.getAdapter().getCount()>0) {
                mListView.performItemClick(mListView.getAdapter().getView(0, null, null), 0, 0);
            }
        }
    }

    @Override
    public void setSelectedIndex(int selectedIndex) {
        if (mTwoPane) {
            if (mListView!=null && mListView.getAdapter()!=null
                    && mListView.getAdapter().getCount()>selectedIndex) {
                mListView.setSelection(selectedIndex);
                mListView.setItemChecked(selectedIndex, true);
            }
        }
    }

    @Override
    public void onItemSelected(int businessPartnerId) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putInt(RegisterBusinessPartnerActivity.KEY_BUSINESS_PARTNER_ID, businessPartnerId);
            RegisterBusinessPartnerFragment fragment = new RegisterBusinessPartnerFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.business_partner_detail_container, fragment, REGISTER_BUSINESS_PARTNER_FRAGMENT_TAG)
                    .commit();
        } else {
            startActivity(new Intent(this, RegisterBusinessPartnerActivity.class)
                    .putExtra(RegisterBusinessPartnerActivity.KEY_BUSINESS_PARTNER_ID, businessPartnerId));
        }
    }

    @Override
    public void onItemLongSelected(final int businessPartnerId, final String businessPartnerName, User user) {
        if(user!=null){
            if(user.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID){
                new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.delete_business_partner, businessPartnerName))
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String result = mUserBusinessPartnerDB.deactivateUserBusinessPartner(businessPartnerId);
                                if (result==null) {
                                    if (mListView != null) {
                                        if (mListView.getAdapter()!=null && (mListView.getAdapter() instanceof BusinessPartnersListAdapter)) {
                                            ((BusinessPartnersListAdapter) mListView.getAdapter())
                                                    .setData(mUserBusinessPartnerDB.getActiveUserBusinessPartners());
                                        } else {
                                            mListView.setAdapter(new BusinessPartnersListAdapter(
                                                    BusinessPartnersListActivity.this, mUserBusinessPartnerDB.getActiveUserBusinessPartners(), 0));
                                        }
                                        if (mTwoPane) {
                                            if(mListView.getAdapter().getCount()>0) {
                                                mListView.performItemClick(mListView.getAdapter().getView(0, null, null), 0, 0);
                                            } else {
                                                getSupportFragmentManager().beginTransaction()
                                                        .replace(R.id.business_partner_detail_container,
                                                                new RegisterBusinessPartnerFragment(), REGISTER_BUSINESS_PARTNER_FRAGMENT_TAG)
                                                        .commit();
                                            }
                                        }
                                    }
                                } else {
                                    Toast.makeText(BusinessPartnersListActivity.this, result, Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
            }else if(user.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID){
                new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.init_session_business_partner, businessPartnerName))
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Utils.setAppCurrentBusinessPartnerId(BusinessPartnersListActivity.this, businessPartnerId);
                                if (mListView.getAdapter() instanceof BusinessPartnersListAdapter) {
                                    ((BusinessPartnersListAdapter) mListView.getAdapter()).setAppCurrentBusinessPartnerId(businessPartnerId);
                                    ((BusinessPartnersListAdapter) mListView.getAdapter()).notifyDataSetChanged();
                                }
                                Toast.makeText(BusinessPartnersListActivity.this, getString(R.string.session_loaded_detail,
                                        businessPartnerName), Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
            }
        }
    }

    @Override
    public void onBusinessPartnerRegistered() {
        reloadBusinessPartnersList();
    }

    @Override
    public void onBusinessPartnerUpdated() {
        if (mListView!=null && mListView.getAdapter()!=null && mUserBusinessPartnerDB!=null) {
            ((BusinessPartnersListAdapter) mListView.getAdapter()).setData(
                    mUserBusinessPartnerDB.getActiveUserBusinessPartners());
        }
    }

}

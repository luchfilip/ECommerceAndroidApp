package com.smartbuilders.smartsales.ecommerceandroidapp;

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

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.BusinessPartnersListAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.UserBusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

/**
 * Jesus Sarco, 03.06.2016
 */
public class BusinessPartnersListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BusinessPartnersListFragment.Callback, RegisterBusinessPartnerFragment.Callback,
        DialogRegisterBusinessPartner.Callback {

    public static final String REGISTER_BUSINESS_PARTNER_FRAGMENT_TAG = "REGISTER_BUSINESS_PARTNER_FRAGMENT_TAG";

    private User mCurrentUser;
    private UserBusinessPartnerDB mUserBusinessPartnerDB;
    private boolean mTwoPane;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_partners_list);

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

        mTwoPane = findViewById(R.id.business_partner_detail_container)!=null;

        mUserBusinessPartnerDB = new UserBusinessPartnerDB(this, mCurrentUser);

        mListView = (ListView) findViewById(R.id.business_partners_list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab!=null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mTwoPane) {
                        //getSupportFragmentManager().beginTransaction()
                        //        .replace(R.id.business_partner_detail_container,
                        //                new RegisterBusinessPartnerFragment(),
                        //                REGISTER_BUSINESS_PARTNER_FRAGMENT_TAG)
                        //        .commit();
                        showDialogCreateBusinessPartner();
                    } else {
                        startActivity(new Intent(BusinessPartnersListActivity.this,
                                RegisterBusinessPartnerActivity.class));
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_business_partners_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                startActivity(new Intent(this, SearchResultsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void reloadBusinessPartnersList() {
        if (mListView != null && mUserBusinessPartnerDB!=null) {
            if (mListView.getAdapter()!=null) {
                ((BusinessPartnersListAdapter) mListView.getAdapter()).setData(
                        mUserBusinessPartnerDB.getActiveUserBusinessPartners());
            } else {
                mListView.setAdapter(new BusinessPartnersListAdapter(this,
                        mUserBusinessPartnerDB.getActiveUserBusinessPartners()));
            }
            if (mTwoPane) {
                mListView.performItemClick(mListView.getAdapter().getView(0, null, null), 0, 0);
            }
        }
    }

    private void showDialogCreateBusinessPartner() {
        DialogRegisterBusinessPartner dialogRegisterBusinessPartner =
                DialogRegisterBusinessPartner.newInstance(mCurrentUser);
        dialogRegisterBusinessPartner.setCancelable(false);
        dialogRegisterBusinessPartner.show(getSupportFragmentManager(),
                DialogRegisterBusinessPartner.class.getSimpleName());
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
    public void onItemLongSelected(final int businessPartnerId, String businessPartnerCommercialName) {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.delete_business_partner, businessPartnerCommercialName))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String result = mUserBusinessPartnerDB.deactivateUserBusinessPartner(businessPartnerId);
                        if (result==null) {
                            if (mListView != null) {
                                if (mListView.getAdapter()!=null) {
                                    ((BusinessPartnersListAdapter) mListView.getAdapter()).setData(mUserBusinessPartnerDB.getActiveUserBusinessPartners());
                                } else {
                                    mListView.setAdapter(new BusinessPartnersListAdapter(BusinessPartnersListActivity.this, mUserBusinessPartnerDB.getActiveUserBusinessPartners()));
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
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    @Override
    public void onBusinessPartnerRegistered() {
        reloadBusinessPartnersList();
    }

    @Override
    public void onBusinessPartnerUpdated() {
        reloadBusinessPartnersList();
    }

}

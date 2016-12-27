package com.smartbuilders.smartsales.ecommerce;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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
        BusinessPartnerDetailsFragment.Callback,
        DialogRegisterUserBusinessPartner.Callback {

    public static final String REGISTER_BUSINESS_PARTNER_FRAGMENT_TAG = "REGISTER_BUSINESS_PARTNER_FRAGMENT_TAG";
    public static final String BUSINESS_PARTNER_DETAILS_FRAGMENT_TAG = "BUSINESS_PARTNER_DETAILS_FRAGMENT_TAG";

    private UserBusinessPartnerDB mUserBusinessPartnerDB;
    private BusinessPartnerDB mBusinessPartnerDB;
    private boolean mTwoPane;
    private ListView mListView;
    private User mUser;
    private Integer mBusinessPartnerIdInDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_partners_list);

        mUser = Utils.getCurrentUser(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.setCustomToolbarTitle(this, toolbar, true);
        setSupportActionBar(toolbar);

        Utils.inflateNavigationView(this, this, toolbar, mUser);

        //por cuestiones esteticas se carga el spinner de una vez aqui para que se coloque la barra
        //de filtrar del tamaño definitivo
        final Spinner filterByOptionsSpinner = (Spinner) findViewById(R.id.filter_by_options_spinner);
        final ArrayAdapter<CharSequence> adapter;
        if (mUser.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID) {
            adapter = ArrayAdapter.createFromResource(
                    this, R.array.filter_user_business_partner_by_options, R.layout.spinner_custom_prompt_item);
        } else {
            adapter = ArrayAdapter.createFromResource(
                    this, R.array.filter_business_partner_by_options, R.layout.spinner_custom_prompt_item);
        }
        if(filterByOptionsSpinner!=null) {
            adapter.setDropDownViewResource(R.layout.spinner_custom_item);
            filterByOptionsSpinner.setAdapter(adapter);
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
                            mUserBusinessPartnerDB.getUserBusinessPartners());
                } else {
                    mListView.setAdapter(new BusinessPartnersListAdapter(this, mUser,
                            mUserBusinessPartnerDB.getUserBusinessPartners(), 0));
                }
            }else if(mBusinessPartnerDB != null){
                if (mListView.getAdapter() != null) {
                    ((BusinessPartnersListAdapter) mListView.getAdapter()).setData(
                            mBusinessPartnerDB.getBusinessPartners());
                } else {
                    try{
                        mListView.setAdapter(new BusinessPartnersListAdapter(this, mUser,
                                mBusinessPartnerDB.getBusinessPartners(),
                                Utils.getAppCurrentBusinessPartnerId(this, mUser)));
                    }catch (Exception e){
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        onListIsLoaded();
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
        } else if (findViewById(R.id.filter_businessPartner_editText) != null
                && !TextUtils.isEmpty(((EditText) findViewById(R.id.filter_businessPartner_editText)).getText())){
            ((EditText) findViewById(R.id.filter_businessPartner_editText)).setText(null);
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
            if (mListView != null && mListView.getAdapter()!=null && !mListView.getAdapter().isEmpty()) {
                mListView.performItemClick(mListView.getAdapter().getView(0, null, null), 0, 0);
            }
        }
        showOrHideEmptyLayoutWallpaper();
    }

    private void showOrHideEmptyLayoutWallpaper() {
        if (mListView==null || mListView.getAdapter()==null || mListView.getAdapter().isEmpty()) {
            if (findViewById(R.id.empty_layout_wallpaper) != null) {
                findViewById(R.id.empty_layout_wallpaper).setVisibility(View.VISIBLE);
            }
            if (findViewById(R.id.business_partner_detail_container) != null) {
                findViewById(R.id.business_partner_detail_container).setVisibility(View.GONE);
            }
            if (mListView != null) {
                mListView.setVisibility(View.GONE);
            }
        } else {
            if (mListView != null) {
                mListView.setVisibility(View.VISIBLE);
            }
            if (findViewById(R.id.business_partner_detail_container) != null) {
                findViewById(R.id.business_partner_detail_container).setVisibility(View.VISIBLE);
            }
            if (findViewById(R.id.empty_layout_wallpaper) != null) {
                findViewById(R.id.empty_layout_wallpaper).setVisibility(View.GONE);
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
    public Integer getBusinessPartnerIdInDetailFragment() {
        return mBusinessPartnerIdInDetailFragment;
    }

    @Override
    public void onItemSelected(int businessPartnerId, User user) {
        if(BuildConfig.IS_SALES_FORCE_SYSTEM || user.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID){
            if (mTwoPane) {
                mBusinessPartnerIdInDetailFragment = businessPartnerId;
                Bundle args = new Bundle();
                args.putInt(BusinessPartnerDetailsActivity.KEY_BUSINESS_PARTNER_ID, businessPartnerId);
                BusinessPartnerDetailsFragment fragment = new BusinessPartnerDetailsFragment();
                fragment.setArguments(args);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.business_partner_detail_container, fragment, BUSINESS_PARTNER_DETAILS_FRAGMENT_TAG)
                        .commit();
            } else {
                startActivity(new Intent(this, BusinessPartnerDetailsActivity.class)
                        .putExtra(BusinessPartnerDetailsActivity.KEY_BUSINESS_PARTNER_ID, businessPartnerId));
            }
        } else {
            if (mTwoPane) {
                mBusinessPartnerIdInDetailFragment = businessPartnerId;
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
    }

    //@Override
    //public void onItemLongSelected(final int businessPartnerId, final String businessPartnerName, User user) {
    //    if(user!=null){
    //        if(BuildConfig.IS_SALES_FORCE_SYSTEM || user.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID){
    //            new AlertDialog.Builder(this)
    //                    .setMessage(getString(R.string.init_session_business_partner_question, businessPartnerName))
    //                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
    //                        public void onClick(DialogInterface dialog, int which) {
    //                            Utils.setAppCurrentBusinessPartnerId(BusinessPartnersListActivity.this, businessPartnerId);
    //                            if (mListView.getAdapter() instanceof BusinessPartnersListAdapter) {
    //                                ((BusinessPartnersListAdapter) mListView.getAdapter()).setAppCurrentBusinessPartnerId(businessPartnerId);
    //                                ((BusinessPartnersListAdapter) mListView.getAdapter()).notifyDataSetChanged();
    //                            }
    //                            Toast.makeText(BusinessPartnersListActivity.this, getString(R.string.session_loaded_detail,
    //                                    businessPartnerName), Toast.LENGTH_SHORT).show();
    //                        }
    //                    })
    //                    .setNegativeButton(R.string.no, null)
    //                    .show();
    //        } else if(user.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID){
    //            new AlertDialog.Builder(this)
    //                    .setMessage(getString(R.string.delete_business_partner, businessPartnerName))
    //                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
    //                        public void onClick(DialogInterface dialog, int which) {
    //                            String result = mUserBusinessPartnerDB.deactivateUserBusinessPartner(businessPartnerId);
    //                            if (result==null) {
    //                                reloadBusinessPartnersList();
    //                            } else {
    //                                Toast.makeText(BusinessPartnersListActivity.this, result, Toast.LENGTH_SHORT).show();
    //                            }
    //                        }
    //                    })
    //                    .setNegativeButton(R.string.cancel, null)
    //                    .show();
    //        }
    //    }
    //}

    @Override
    public void onBusinessPartnerRegistered() {
        reloadBusinessPartnersList();
    }

    @Override
    public void onBusinessPartnerUpdated() {
        if (mListView!=null && mListView.getAdapter()!=null && mUserBusinessPartnerDB!=null) {
            ((BusinessPartnersListAdapter) mListView.getAdapter()).setData(
                    mUserBusinessPartnerDB.getUserBusinessPartners());
        }
    }

    @Override
    public void onBusinessPartnerSelected(int businessPartnerId) {
        if (mListView!=null && mListView.getAdapter()!=null) {
            ((BusinessPartnersListAdapter) mListView.getAdapter()).setAppCurrentBusinessPartnerId(businessPartnerId);
            ((BusinessPartnersListAdapter) mListView.getAdapter()).notifyDataSetChanged();
        }
    }
}

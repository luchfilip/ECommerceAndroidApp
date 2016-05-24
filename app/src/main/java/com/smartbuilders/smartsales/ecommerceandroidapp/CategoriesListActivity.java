package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductCategory;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

public class CategoriesListActivity extends AppCompatActivity implements
        CategoriesListFragment.Callback, NavigationView.OnNavigationItemSelectedListener {

    private static final String SUBCATEGORYFRAGMENT_TAG = "SUBCATEGORYFRAGMENT_TAG";
    public static final String KEY_CURRENT_USER = "KEY_CURRENT_USER";
    private static final String STATE_CURRENT_USER = "state_current_user";

    private boolean mTwoPane;
    private User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_list);

        if( savedInstanceState != null ) {
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mCurrentUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
        }

        if(getIntent()!=null && getIntent().getExtras()!=null){
            if(getIntent().getExtras().containsKey(KEY_CURRENT_USER)){
                mCurrentUser = getIntent().getExtras().getParcelable(KEY_CURRENT_USER);
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.setCustomToolbarTitle(this, toolbar, mCurrentUser, true);
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

        if(findViewById(R.id.subcategory_list_container) != null){
            // If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if(savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.subcategory_list_container, new SubCategoriesListFragment(),
                                SUBCATEGORYFRAGMENT_TAG)
                        .commit();
            }
        }else{
            mTwoPane = false;
        }
    }

    @Override
    public void onItemSelected(ProductCategory productCategory) {
        if(mTwoPane){
            Bundle args = new Bundle();
            args.putInt(SubCategoriesListFragment.KEY_CATEGORY_ID, productCategory.getId());
            args.putParcelable(SubCategoriesListActivity.KEY_CURRENT_USER, mCurrentUser);
            SubCategoriesListFragment fragment = new SubCategoriesListFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.subcategory_list_container, fragment, SUBCATEGORYFRAGMENT_TAG)
                    .commit();
        }else{
            Intent intent = new Intent(CategoriesListActivity.this, SubCategoriesListActivity.class);
            intent.putExtra(SubCategoriesListFragment.KEY_CATEGORY_ID, productCategory.getId());
            intent.putExtra(SubCategoriesListActivity.KEY_CURRENT_USER, mCurrentUser);
            startActivity(intent);
        }
    }

    @Override
    public void onItemLongSelected(ProductCategory productCategory){
        Intent intent = new Intent(CategoriesListActivity.this, ProductsListActivity.class);
        intent.putExtra(ProductsListActivity.KEY_PRODUCT_CATEGORY_ID, productCategory.getId());
        intent.putExtra(ProductsListActivity.KEY_CURRENT_USER, mCurrentUser);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mCurrentUser);
        super.onSaveInstanceState(outState);
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

}

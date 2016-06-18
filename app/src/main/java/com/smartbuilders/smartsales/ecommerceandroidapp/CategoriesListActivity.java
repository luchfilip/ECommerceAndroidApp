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
import android.widget.ListView;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductCategory;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;

/**
 * Jesus Sarco, before 10.06.2016
 */
public class CategoriesListActivity extends AppCompatActivity implements
        CategoriesListFragment.Callback, NavigationView.OnNavigationItemSelectedListener {

    private static final String SUBCATEGORYFRAGMENT_TAG = "SUBCATEGORYFRAGMENT_TAG";

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_list);

        User currentUser = Utils.getCurrentUser(this);

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
                    .setText(getString(R.string.welcome_user, currentUser.getUserName()));

        mTwoPane = findViewById(R.id.subcategory_list_container) != null;
    }

    @Override
    public void onCategoriesListIsLoaded() {
        if (mTwoPane) {
            ListView lv = (ListView) findViewById(R.id.categories_list);
            if (lv!=null && lv.getAdapter()!=null && lv.getAdapter().getCount()>0) {
                lv.performItemClick(lv.getAdapter().getView(0, null, null), 0, 0);
            }
        }
    }

    @Override
    public void setSelectedIndex(int selectedIndex) {
        if (mTwoPane) {
            ListView lv = (ListView) findViewById(R.id.categories_list);
            if (lv!=null && lv.getAdapter()!=null && lv.getAdapter().getCount()>selectedIndex) {
                lv.setSelection(selectedIndex);
                lv.setItemChecked(selectedIndex, true);
            }
        }
    }

    @Override
    public void onItemSelected(ProductCategory productCategory) {
        if(mTwoPane){
            Bundle args = new Bundle();
            args.putInt(SubCategoriesListActivity.KEY_CATEGORY_ID, productCategory.getId());
            SubCategoriesListFragment fragment = new SubCategoriesListFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.subcategory_list_container, fragment, SUBCATEGORYFRAGMENT_TAG)
                    .commit();
        }else{
            Intent intent = new Intent(this, SubCategoriesListActivity.class);
            intent.putExtra(SubCategoriesListActivity.KEY_CATEGORY_ID, productCategory.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onItemLongSelected(ProductCategory productCategory){
        Intent intent = new Intent(this, ProductsListActivity.class);
        intent.putExtra(ProductsListActivity.KEY_PRODUCT_CATEGORY_ID, productCategory.getId());
        startActivity(intent);
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

}

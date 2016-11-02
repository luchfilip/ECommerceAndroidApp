package com.smartbuilders.smartsales.ecommerce;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.smartbuilders.smartsales.ecommerce.utils.Utils;

/**
 * Jesus Sarco, before 10.06.2016
 */
public class CategoriesListActivity extends AppCompatActivity implements CategoriesListFragment.Callback {

    private static final String SUBCATEGORY_FRAGMENT_TAG = "SUBCATEGORY_FRAGMENT_TAG";

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_list);

        mTwoPane = findViewById(R.id.subcategory_list_container) != null;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.setCustomToolbarTitle(this, toolbar, false);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onCategoriesListIsLoaded() {
        if (mTwoPane) {
            ListView lv = (ListView) findViewById(R.id.categories_list);
            if (lv!=null && lv.getAdapter()!=null && !lv.getAdapter().isEmpty()) {
                lv.performItemClick(lv.getAdapter().getView(0, null, null), 0, 0);
            } else {
                if (findViewById(R.id.subcategory_list_container) != null) {
                    findViewById(R.id.subcategory_list_container).setVisibility(View.GONE);
                }
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
    public void onItemSelected(int productCategoryId) {
        if(mTwoPane){
            Bundle args = new Bundle();
            args.putInt(SubCategoriesListActivity.KEY_CATEGORY_ID, productCategoryId);
            SubCategoriesListFragment fragment = new SubCategoriesListFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.subcategory_list_container, fragment, SUBCATEGORY_FRAGMENT_TAG)
                    .commit();
        }else{
            Intent intent = new Intent(this, SubCategoriesListActivity.class);
            intent.putExtra(SubCategoriesListActivity.KEY_CATEGORY_ID, productCategoryId);
            startActivity(intent);
        }
    }

    @Override
    public void onItemLongSelected(int productCategoryId){
        Intent intent = new Intent(this, ProductsListActivity.class);
        intent.putExtra(ProductsListActivity.KEY_PRODUCT_CATEGORY_ID, productCategoryId);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

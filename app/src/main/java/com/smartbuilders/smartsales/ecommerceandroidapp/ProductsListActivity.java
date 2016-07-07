package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.model.UserProfile;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.ProductsListAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.ProductDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;

import java.util.ArrayList;

/**
 * Jesus Sarco
 */
public class ProductsListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DialogSortProductListOptions.Callback {

    public static final String KEY_PRODUCT_CATEGORY_ID = "KEY_PRODUCT_CATEGORY_ID";
    public static final String KEY_PRODUCT_SUBCATEGORY_ID = "KEY_PRODUCT_SUBCATEGORY_ID";
    public static final String KEY_PRODUCT_BRAND_ID = "KEY_PRODUCT_BRAND_ID";
    public static final String KEY_PRODUCT_NAME = "KEY_PRODUCT_NAME";
    public static final String KEY_SEARCH_PATTERN = "KEY_SEARCH_PATTERN";
    private static final String STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION = "STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION";
    private static final String STATE_CURRENT_PRODUCTS_LIST_ADAPTER_MASK = "STATE_CURRENT_PRODUCTS_LIST_ADAPTER_MASK";
    private static final String STATE_CURRENT_SORT_OPTION = "STATE_CURRENT_SORT_OPTION";
    private static final String STATE_CURRENT_FILTER_TEXT = "STATE_CURRENT_FILTER_TEXT";
    private static final String STATE_CURRENT_FILTER_OPTION = "STATE_CURRENT_FILTER_OPTION";
    private static final String STATE_SPINNER_SELECTED_ITEM_POSITION = "STATE_SPINNER_SELECTED_ITEM_POSITION";

    private int productCategoryId;
    private int productSubCategoryId;
    private int productBrandId;
    private String productName;
    private String mSearchPattern;
    private ArrayList<Product> products;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    private int mRecyclerViewCurrentFirstPosition;
    private int mCurrentProductsListAdapterMask = ProductsListAdapter.MASK_PRODUCT_DETAILS;
    private int mCurrentSortOption;
    private String mCurrentFilterText;
    private int mCurrentFilterOption;
    private int mSpinnerSelectedItemPosition;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_list);

        final User user = Utils.getCurrentUser(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.setCustomToolbarTitle(this, toolbar, true);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if(navigationView!=null && user!=null){
            if(user.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID){
                navigationView.inflateMenu(R.menu.business_partner_drawer_menu);
            }else if(user.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID){
                navigationView.inflateMenu(R.menu.sales_man_drawer_menu);
            }
            navigationView.setNavigationItemSelectedListener(this);
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name))
                    .setText(getString(R.string.welcome_user, user.getUserName()));
        }

        //por cuestiones esteticas se carga el spinner de una vez aqui para que se coloque la barra
        //de filtrar del tamaño definitivo
        final Spinner filterByOptionsSpinner = (Spinner) findViewById(R.id.filter_by_options_spinner);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.filter_by_options, R.layout.spinner_custom_prompt_item);
        if(filterByOptionsSpinner!=null && adapter!=null) {
            adapter.setDropDownViewResource(R.layout.spinner_custom_item);
            filterByOptionsSpinner.setAdapter(adapter);
        }

        new Thread() {
            @Override
            public void run() {
                try {
                    if(savedInstanceState != null) {
                        if(savedInstanceState.containsKey(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION)){
                            mRecyclerViewCurrentFirstPosition = savedInstanceState.getInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION);
                        }
                        if(savedInstanceState.containsKey(STATE_CURRENT_PRODUCTS_LIST_ADAPTER_MASK)){
                            mCurrentProductsListAdapterMask = savedInstanceState.getInt(STATE_CURRENT_PRODUCTS_LIST_ADAPTER_MASK);
                        }
                        if(savedInstanceState.containsKey(STATE_CURRENT_SORT_OPTION)){
                            mCurrentSortOption = savedInstanceState.getInt(STATE_CURRENT_SORT_OPTION);
                        }
                        if(savedInstanceState.containsKey(STATE_CURRENT_FILTER_TEXT)){
                            mCurrentFilterText = savedInstanceState.getString(STATE_CURRENT_FILTER_TEXT);
                        }
                        if(savedInstanceState.containsKey(STATE_CURRENT_FILTER_OPTION)){
                            mCurrentFilterOption = savedInstanceState.getInt(STATE_CURRENT_FILTER_OPTION);
                        }
                        if(savedInstanceState.containsKey(STATE_SPINNER_SELECTED_ITEM_POSITION)){
                            mSpinnerSelectedItemPosition = savedInstanceState.getInt(STATE_SPINNER_SELECTED_ITEM_POSITION);
                        }
                    }

                    if(getIntent()!=null && getIntent().getExtras()!=null) {
                        if(getIntent().getExtras().containsKey(KEY_PRODUCT_CATEGORY_ID)){
                            productCategoryId = getIntent().getExtras().getInt(KEY_PRODUCT_CATEGORY_ID);
                        }
                        if(getIntent().getExtras().containsKey(KEY_PRODUCT_SUBCATEGORY_ID)){
                            productSubCategoryId = getIntent().getExtras().getInt(KEY_PRODUCT_SUBCATEGORY_ID);
                        }
                        if(getIntent().getExtras().containsKey(KEY_PRODUCT_BRAND_ID)){
                            productBrandId = getIntent().getExtras().getInt(KEY_PRODUCT_BRAND_ID);
                        }
                        if(getIntent().getExtras().containsKey(KEY_PRODUCT_NAME)){
                            productName = getIntent().getExtras().getString(KEY_PRODUCT_NAME);
                        }
                        if(getIntent().getExtras().containsKey(KEY_SEARCH_PATTERN)){
                            mSearchPattern = getIntent().getExtras().getString(KEY_SEARCH_PATTERN);
                        }
                    }

                    products = new ArrayList<>();

                    if (productCategoryId != 0) {
                        products.addAll(new ProductDB(ProductsListActivity.this, user).getProductsByCategoryId(productCategoryId));
                    } else if (productSubCategoryId != 0) {
                        products.addAll(new ProductDB(ProductsListActivity.this, user).getProductsBySubCategoryId(productSubCategoryId, mSearchPattern));
                    } else if (productBrandId != 0) {
                        products.addAll(new ProductDB(ProductsListActivity.this, user).getProductsByBrandId(productBrandId));
                    } else if (productName != null) {
                        products.addAll(new ProductDB(ProductsListActivity.this, user).getProductsByName(productName));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mRecyclerView = (RecyclerView) findViewById(R.id.product_list_result);

                            final ImageView changeLayoutImageButton = (ImageView) findViewById(R.id.change_layout_button);
                            if(changeLayoutImageButton!=null){
                                if(mCurrentProductsListAdapterMask==ProductsListAdapter.MASK_PRODUCT_LARGE_DETAILS){
                                    changeLayoutImageButton.setImageResource(R.drawable.ic_view_module_black_24dp);
                                }else{
                                    changeLayoutImageButton.setImageResource(R.drawable.ic_view_agenda_black_24dp);
                                }

                                changeLayoutImageButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mRecyclerViewCurrentFirstPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
                                        if(mCurrentProductsListAdapterMask==ProductsListAdapter.MASK_PRODUCT_LARGE_DETAILS){
                                            mCurrentProductsListAdapterMask = ProductsListAdapter.MASK_PRODUCT_DETAILS;
                                            if (useGridView()) {
                                                if(!(mLinearLayoutManager instanceof GridLayoutManager)){
                                                    mLinearLayoutManager = new GridLayoutManager(ProductsListActivity.this, getSpanCount());
                                                }
                                            }else{
                                                if(mLinearLayoutManager instanceof GridLayoutManager){
                                                    mLinearLayoutManager = new LinearLayoutManager(ProductsListActivity.this);
                                                }
                                            }
                                            changeLayoutImageButton.setImageResource(R.drawable.ic_view_agenda_black_24dp);
                                        }else{
                                            mCurrentProductsListAdapterMask = ProductsListAdapter.MASK_PRODUCT_LARGE_DETAILS;
                                            if(mLinearLayoutManager instanceof GridLayoutManager){
                                                mLinearLayoutManager = new LinearLayoutManager(ProductsListActivity.this);
                                            }
                                            changeLayoutImageButton.setImageResource(R.drawable.ic_view_module_black_24dp);
                                        }
                                        mRecyclerView.setLayoutManager(mLinearLayoutManager);
                                        mRecyclerView.setAdapter(new ProductsListAdapter(ProductsListActivity.this,
                                                ProductsListActivity.this, products, mCurrentProductsListAdapterMask, mCurrentSortOption, user));
                                        mRecyclerView.scrollToPosition(mRecyclerViewCurrentFirstPosition);
                                    }
                                });
                            }

                            final ImageView sortProductsListImageButton = (ImageView) findViewById(R.id.sort_products_list_button);
                            if(sortProductsListImageButton!=null){
                                sortProductsListImageButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DialogSortProductListOptions dialogSortProductListOptions =
                                                DialogSortProductListOptions.newInstance(user, mCurrentSortOption);
                                        dialogSortProductListOptions.show(ProductsListActivity.this.getSupportFragmentManager(),
                                                DialogUpdateShoppingCartQtyOrdered.class.getSimpleName());
                                    }
                                });
                            }

                            TextView categorySubcategoryResultsTextView = (TextView) findViewById(R.id.category_subcategory_results);
                            if(categorySubcategoryResultsTextView!=null){
                                if(!products.isEmpty()) {
                                    if (productCategoryId != 0) {
                                        if(products.get(0).getProductCategory()!=null){
                                            categorySubcategoryResultsTextView.setText(getString(R.string.category_detail,
                                                    products.get(0).getProductCategory().getDescription()));
                                        }
                                    } else if (productSubCategoryId != 0) {
                                        if(products.get(0).getProductSubCategory()!=null){
                                            categorySubcategoryResultsTextView.setText(getString(R.string.subcategory_detail,
                                                    products.get(0).getProductSubCategory().getDescription()));
                                        }
                                    } else if (productBrandId != 0) {
                                        if(products.get(0).getProductBrand()!=null){
                                            categorySubcategoryResultsTextView.setText(getString(R.string.brand_detail,
                                                    products.get(0).getProductBrand().getDescription()));
                                        }
                                    } else if (productName != null) {
                                        categorySubcategoryResultsTextView.setText(getString(R.string.search_pattern_detail,
                                                productName));
                                    }
                                } else {
                                    categorySubcategoryResultsTextView.setText(getString(R.string.no_products_to_show));
                                }
                            }

                            // use this setting to improve performance if you know that changes
                            // in content do not change the layout size of the RecyclerView
                            mRecyclerView.setHasFixedSize(true);
                            if (mCurrentProductsListAdapterMask!=ProductsListAdapter.MASK_PRODUCT_LARGE_DETAILS
                                    && useGridView()) {
                                mLinearLayoutManager = new GridLayoutManager(ProductsListActivity.this, getSpanCount());
                            }else{
                                mLinearLayoutManager = new LinearLayoutManager(ProductsListActivity.this);
                            }
                            mRecyclerView.setLayoutManager(mLinearLayoutManager);
                            mRecyclerView.setAdapter(new ProductsListAdapter(ProductsListActivity.this,
                                    ProductsListActivity.this, products, mCurrentProductsListAdapterMask, mCurrentSortOption, user));

                            if (mRecyclerViewCurrentFirstPosition!=0) {
                                mRecyclerView.scrollToPosition(mRecyclerViewCurrentFirstPosition);
                            }

                            if(findViewById(R.id.filter_bar_linear_layout)!=null){
                                final ImageView filterImageView = (ImageView) findViewById(R.id.filter_imageView);

                                final TextView productsListSize = (TextView) findViewById(R.id.products_list_size);
                                if(productsListSize!=null){
                                    productsListSize.setText(getString(R.string.products_list_size_details,
                                            String.valueOf(mRecyclerView.getAdapter().getItemCount())));
                                }

                                if(filterByOptionsSpinner!=null && adapter!=null){
                                    filterByOptionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            mSpinnerSelectedItemPosition = position;
                                            final String selectedOption = (String) parent.getItemAtPosition(position);
                                            if(selectedOption!=null){
                                                if(selectedOption.equals(getString(R.string.filter_by_product_name))){
                                                    mCurrentFilterOption = ProductsListAdapter.FILTER_BY_PRODUCT_NAME;
                                                }else if(selectedOption.equals(getString(R.string.filter_by_product_internal_code))){
                                                    mCurrentFilterOption = ProductsListAdapter.FILTER_BY_PRODUCT_INTERNAL_CODE;
                                                }else if(selectedOption.equals(getString(R.string.filter_by_product_brand_description))){
                                                    mCurrentFilterOption = ProductsListAdapter.FILTER_BY_PRODUCT_BRAND_DESCRIPTION;
                                                }else if(selectedOption.equals(getString(R.string.filter_by_product_description))){
                                                    mCurrentFilterOption = ProductsListAdapter.FILTER_BY_PRODUCT_DESCRIPTION;
                                                }else if(selectedOption.equals(getString(R.string.filter_by_product_purpose))){
                                                    mCurrentFilterOption = ProductsListAdapter.FILTER_BY_PRODUCT_PURPOSE;
                                                }
                                                if(!TextUtils.isEmpty(mCurrentFilterText)){
                                                    ((ProductsListAdapter) mRecyclerView.getAdapter()).filter(mCurrentFilterText, mCurrentFilterOption);
                                                    if(productsListSize!=null){
                                                        productsListSize.setText(getString(R.string.products_list_size_details,
                                                                String.valueOf(mRecyclerView.getAdapter().getItemCount())));
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) { }
                                    });
                                    filterByOptionsSpinner.setSelection(mSpinnerSelectedItemPosition);
                                }

                                final EditText filterProduct = (EditText) findViewById(R.id.filter_product_editText);
                                if(filterProduct!=null && filterImageView!=null && productsListSize!=null) {
                                    filterProduct.setFocusableInTouchMode(true);

                                    final View.OnClickListener filterImageViewOnClickListener =
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    filterProduct.setText(null);
                                                }
                                            };
                                    filterProduct.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            if(s.length()>0){
                                                filterImageView.setImageResource(R.drawable.ic_close_black_24dp);
                                                filterImageView.setOnClickListener(filterImageViewOnClickListener);
                                            }else{
                                                filterImageView.setImageResource(R.drawable.ic_filter_list_black_24dp);
                                                filterImageView.setOnClickListener(null);
                                            }
                                            mCurrentFilterText = s.toString();
                                            ((ProductsListAdapter) mRecyclerView.getAdapter()).filter(mCurrentFilterText, mCurrentFilterOption);
                                            productsListSize.setText(getString(R.string.products_list_size_details,
                                                        String.valueOf(mRecyclerView.getAdapter().getItemCount())));
                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) { }
                                    });
                                    filterProduct.setText(mCurrentFilterText);
                                    filterProduct.setSelection(filterProduct.length());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            findViewById(R.id.product_list_result).setVisibility(View.VISIBLE);
                            findViewById(R.id.progressContainer).setVisibility(View.GONE);
                        }
                    }
                });
            }
        }.start();
    }

    private boolean useGridView(){
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            return true;
        }else {
            switch(getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) {
                case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                    switch(getResources().getDisplayMetrics().densityDpi) {
                        case DisplayMetrics.DENSITY_LOW:
                            break;
                        case DisplayMetrics.DENSITY_MEDIUM:
                        case DisplayMetrics.DENSITY_HIGH:
                        case DisplayMetrics.DENSITY_XHIGH:
                            return  true;
                    }
                    break;
                //case Configuration.SCREENLAYOUT_SIZE_LARGE:
                //    break;
                //case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                //    break;
                //case Configuration.SCREENLAYOUT_SIZE_SMALL:
                //    break;
            }
        }
        return false;
    }

    private int getSpanCount() {
        switch(getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) {
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                switch(getResources().getDisplayMetrics().densityDpi) {
                    case DisplayMetrics.DENSITY_LOW:
                        break;
                    case DisplayMetrics.DENSITY_MEDIUM:
                    case DisplayMetrics.DENSITY_HIGH:
                    case DisplayMetrics.DENSITY_XHIGH:
                        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                            return 3;
                        }
                        break;
                }
                break;
            //case Configuration.SCREENLAYOUT_SIZE_LARGE:
            //    break;
            //case Configuration.SCREENLAYOUT_SIZE_NORMAL:
            //    break;
            //case Configuration.SCREENLAYOUT_SIZE_SMALL:
            //    break;
        }
        return 2;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_products_list, menu);
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer!=null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_CURRENT_PRODUCTS_LIST_ADAPTER_MASK, mCurrentProductsListAdapterMask);
        try {
            if (mLinearLayoutManager instanceof GridLayoutManager
                    || mCurrentProductsListAdapterMask==ProductsListAdapter.MASK_PRODUCT_LARGE_DETAILS) {
                outState.putInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION,
                        mLinearLayoutManager.findFirstVisibleItemPosition());
            } else {
                outState.putInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION,
                        mLinearLayoutManager.findFirstCompletelyVisibleItemPosition());
            }
        } catch (Exception e) {
            outState.putInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION, mRecyclerViewCurrentFirstPosition);
        }
        outState.putInt(STATE_CURRENT_SORT_OPTION, mCurrentSortOption);
        outState.putString(STATE_CURRENT_FILTER_TEXT, mCurrentFilterText);
        outState.putInt(STATE_CURRENT_FILTER_OPTION, mCurrentFilterOption);
        outState.putInt(STATE_SPINNER_SELECTED_ITEM_POSITION, mSpinnerSelectedItemPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void sortProductsList(int sortOption, User user) {
        if(mRecyclerView!=null && mRecyclerView.getAdapter() instanceof ProductsListAdapter){
            mCurrentSortOption = sortOption;
            mRecyclerView.setAdapter(new ProductsListAdapter(this, this, products,
                    mCurrentProductsListAdapterMask, mCurrentSortOption, user));
        }
    }
}
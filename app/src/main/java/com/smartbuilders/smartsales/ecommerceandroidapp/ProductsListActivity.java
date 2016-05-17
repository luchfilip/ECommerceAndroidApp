package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.app.ProgressDialog;
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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.ProductRecyclerViewAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.ProductDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

/**
 * Jesus Sarco
 */
public class ProductsListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener  {

    private static final String TAG = ProductsListActivity.class.getSimpleName();
    public static final String KEY_CURRENT_USER = "KEY_CURRENT_USER";
    private static final String STATE_CURRENT_USER = "state_current_user";
    public static final String KEY_PRODUCT_CATEGORY_ID = "KEY_PRODUCT_CATEGORY_ID";
    public static final String KEY_PRODUCT_SUBCATEGORY_ID = "KEY_PRODUCT_SUBCATEGORY_ID";
    public static final String KEY_PRODUCT_BRAND_ID = "KEY_PRODUCT_BRAND_ID";
    public static final String KEY_PRODUCT_NAME = "KEY_PRODUCT_NAME";
    public static final String KEY_PRODUCT_ID = "KEY_PRODUCT_ID";

    private ProgressDialog waitPlease;
    private User mCurrentUser;
    private int productCategoryId;
    private int productSubCategoryId;
    private int productBrandId;
    private int productId;
    private String productName;
    private ArrayList<Product> products;
    private ProductRecyclerViewAdapter mProductRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_list);

        if( savedInstanceState != null) {
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mCurrentUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
        }

        if(getIntent()!=null && getIntent().getExtras()!=null) {
            if(getIntent().getExtras().containsKey(KEY_PRODUCT_ID)){
                productId = getIntent().getExtras().getInt(KEY_PRODUCT_ID);
            }
            if(getIntent().getExtras().containsKey(KEY_CURRENT_USER)) {
                mCurrentUser = getIntent().getExtras().getParcelable(KEY_CURRENT_USER);
            }
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
        }

        waitPlease = ProgressDialog.show(this, getString(R.string.loading), getString(R.string.wait_please), true, false);
        new Thread() {
            @Override
            public void run() {
                try {
                    if (productCategoryId != 0) {
                        products = new ProductDB(ProductsListActivity.this, mCurrentUser).getProductsByCategoryId(productCategoryId);
                    } else if (productSubCategoryId != 0) {
                        products = new ProductDB(ProductsListActivity.this, mCurrentUser).getProductsBySubCategoryId(productSubCategoryId);
                    } else if (productBrandId != 0) {
                        products = (new ProductDB(ProductsListActivity.this, mCurrentUser)).getProductsByBrandId(productBrandId);
                    } else if (productName != null) {
                        products = (new ProductDB(ProductsListActivity.this, mCurrentUser)).getProductsByName(productName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                            toolbar.setTitle(R.string.app_name);
                            Utils.setCustomToolbarTitle(ProductsListActivity.this, toolbar, mCurrentUser, true);
                            setSupportActionBar(toolbar);

                            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(ProductsListActivity.this,
                                    drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                            drawer.setDrawerListener(toggle);
                            toggle.syncState();

                            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                            navigationView.setNavigationItemSelectedListener(ProductsListActivity.this);
                            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name))
                                    .setText(getString(R.string.welcome_user, mCurrentUser.getUserName()));

                            if(products!=null && !products.isEmpty()) {
                                if (productCategoryId != 0) {
                                    TextView categorySubcategoryResultsTextView = (TextView) findViewById(R.id.category_subcategory_results);
                                    Spannable word = new SpannableString(products.get(0).getProductCategory().getDescription() + " ");
                                    word.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.product_category)), 0,
                                            word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    categorySubcategoryResultsTextView.setText(word);
                                    categorySubcategoryResultsTextView.append(new SpannableString(" ("+products.size()+" Resultados) "));
                                } else if (productSubCategoryId != 0) {
                                    TextView categorySubcategoryResultsTextView = (TextView) findViewById(R.id.category_subcategory_results);
                                    Spannable word = new SpannableString(products.get(0).getProductCategory().getDescription() + " >> ");
                                    word.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.product_category)), 0,
                                            word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    categorySubcategoryResultsTextView.setText(word);
                                    Spannable wordTwo = new SpannableString(" "+products.get(0).getProductSubCategory().getDescription()+" ");
                                    wordTwo.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.product_subcategory)),
                                            0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    categorySubcategoryResultsTextView.append(wordTwo);
                                    categorySubcategoryResultsTextView.append(new SpannableString(" ("+products.size()+" Resultados) "));
                                } else if (productBrandId != 0) {
                                    TextView categorySubcategoryResultsTextView = (TextView) findViewById(R.id.category_subcategory_results);
                                    Spannable word = new SpannableString(products.get(0).getProductBrand().getDescription() + " ");
                                    word.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.product_category)), 0,
                                            word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    categorySubcategoryResultsTextView.setText(word);
                                    categorySubcategoryResultsTextView.append(new SpannableString("("+products.size()+" Resultados) "));
                                } else if (productName != null) {
                                    TextView categorySubcategoryResultsTextView = (TextView) findViewById(R.id.category_subcategory_results);
                                    Spannable word = new SpannableString("Búsqueda: \""+productName+"\" ");
                                    word.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.product_category)), 0,
                                            word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    categorySubcategoryResultsTextView.setText(word);
                                    categorySubcategoryResultsTextView.append(new SpannableString("("+products.size()+" Resultados) "));
                                }
                            } else if(products==null){
                                products = new ArrayList<>();
                            }

                            if(products.isEmpty()){
                                TextView categorySubcategoryResultsTextView = (TextView) findViewById(R.id.category_subcategory_results);
                                Spannable word = new SpannableString("No se encontraron productos para mostrar. ");
                                word.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimaryDark)), 0,
                                        word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                categorySubcategoryResultsTextView.append(word);
                            }

                            mProductRecyclerViewAdapter = new ProductRecyclerViewAdapter(products, true,
                                    ProductRecyclerViewAdapter.REDIRECT_PRODUCT_DETAILS, mCurrentUser);

                            RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.product_list_result);
                            // use this setting to improve performance if you know that changes
                            // in content do not change the layout size of the RecyclerView
                            mRecyclerView.setHasFixedSize(true);
                            if (useGridView()) {
                                mRecyclerView.setLayoutManager(new GridLayoutManager(ProductsListActivity.this, getSpanCount()));
                            }else{
                                mRecyclerView.setLayoutManager(new LinearLayoutManager(ProductsListActivity.this));
                            }
                            mRecyclerView.setAdapter(mProductRecyclerViewAdapter);

                            if(!products.isEmpty() && productId != 0){
                                for(int pos = 0; pos < mProductRecyclerViewAdapter.getItemCount(); pos++){
                                    if(mProductRecyclerViewAdapter.getItemId(pos) == productId){
                                        mRecyclerView.scrollToPosition(pos);
                                        break;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                waitPlease.dismiss();
                                waitPlease.cancel();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }.start();
    }

    @Override
    protected void onResume() {
        if(findViewById(R.id.search_bar_linear_layout)!=null){
            findViewById(R.id.search_by_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ProductsListActivity.this, FilterOptionsActivity.class)
                            .putExtra(FilterOptionsActivity.KEY_CURRENT_USER, mCurrentUser));
                }
            });

            findViewById(R.id.search_product_editText).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ProductsListActivity.this, SearchResultsActivity.class)
                            .putExtra(FilterOptionsActivity.KEY_CURRENT_USER, mCurrentUser));
                }
            });

            findViewById(R.id.image_search_bar_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ProductsListActivity.this, SearchResultsActivity.class)
                            .putExtra(FilterOptionsActivity.KEY_CURRENT_USER, mCurrentUser));
                }
            });
        }
        super.onResume();
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
}
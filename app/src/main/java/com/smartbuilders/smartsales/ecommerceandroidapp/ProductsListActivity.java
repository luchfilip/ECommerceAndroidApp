package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.ProductRecyclerViewAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.ProductDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

public class ProductsListActivity extends AppCompatActivity {

    private static final String TAG = ProductsListActivity.class.getSimpleName();
    public static final String KEY_PRODUCT = "key_product";
    public static final String KEY_CURRENT_USER = "KEY_CURRENT_USER";
    public static final String STATE_CURRENT_USER = "state_current_user";
    public static final String KEY_PRODUCT_CATEGORY_ID = "KEY_PRODUCT_CATEGORY_ID";
    public static final String KEY_PRODUCT_SUBCATEGORY_ID = "KEY_PRODUCT_SUBCATEGORY_ID";
    public static final String KEY_PRODUCT_BRAND_ID = "KEY_PRODUCT_BRAND_ID";

    private User mCurrentUser;
    private int productCategoryId;
    private int productSubCategoryId;
    private int productBrandId;

    private ArrayList<Product> products;

    private ProductRecyclerViewAdapter mProductRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getIntent()!=null && getIntent().getExtras()!=null) {
            if(getIntent().getExtras().containsKey(KEY_CURRENT_USER)){
                mCurrentUser = getIntent().getExtras().getParcelable(KEY_CURRENT_USER);

                if(getIntent().getExtras().containsKey(KEY_PRODUCT_CATEGORY_ID)){
                    productCategoryId = getIntent().getExtras().getInt(KEY_PRODUCT_CATEGORY_ID);
                    ProductDB product = new ProductDB(this, mCurrentUser);
                    products = product.getProductsByCategoryId(productCategoryId);
                }

                if(getIntent().getExtras().containsKey(KEY_PRODUCT_SUBCATEGORY_ID)){
                    productSubCategoryId = getIntent().getExtras().getInt(KEY_PRODUCT_SUBCATEGORY_ID);
                    ProductDB product = new ProductDB(this, mCurrentUser);
                    products = product.getProductsBySubCategoryId(productSubCategoryId);
                }

                if(getIntent().getExtras().containsKey(KEY_PRODUCT_BRAND_ID)){
                    productBrandId = getIntent().getExtras().getInt(KEY_PRODUCT_BRAND_ID);
                    ProductDB product = new ProductDB(this, mCurrentUser);
                    products = product.getProductsByBrandId(productBrandId);
                }
            }else{
                Log.e(TAG, "Show Error!");
            }
        }


//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();

        TextView categorySubcategoryResultsTextView = (TextView) findViewById(R.id.category_subcategory_results);
        Spannable word = new SpannableString("Categoria del Producto >> ");

        word.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.product_category)), 0,
                word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        categorySubcategoryResultsTextView.setText(word);
        Spannable wordTwo = new SpannableString(" SubCategoria del producto ");

        wordTwo.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.product_subcategory)),
                0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        categorySubcategoryResultsTextView.append(wordTwo);

        if(products==null){
            products = Utils.getGenericProductsList(20);
        }

        Spannable wordThree = new SpannableString(" ("+(products!=null?products.size():0)+" Resultados) ");
        categorySubcategoryResultsTextView.append(wordThree);

        mProductRecyclerViewAdapter = new ProductRecyclerViewAdapter(products, true,
                ProductRecyclerViewAdapter.REDIRECT_PRODUCT_DETAILS);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.product_list_result);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mProductRecyclerViewAdapter);

        EditText productsListFilter = (EditText) findViewById(R.id.products_list_filter);
        productsListFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mProductRecyclerViewAdapter.filter(s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_products_list, menu);
        //try {
        //    SearchManager searchManager =
        //            (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //    MenuItem searchItem = menu.findItem(R.id.search);
        //    SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //    searchView.setSearchableInfo(
        //            searchManager.getSearchableInfo(getComponentName()));
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // Some code here
                Log.d(TAG, "onQueryTextSubmit("+s+")");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // Some code here
                Log.d(TAG, "onQueryTextChange("+s+")");

                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Some code here
                Log.d(TAG, "onMenuItemActionExpand(...)");
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Some code here
                Log.d(TAG, "onMenuItemActionCollapse(...)");
                return true;
            }
        });

        // Get the search close button
        ImageView closeButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Some code here
                EditText et = (EditText) findViewById(R.id.search_src_text);
                Log.d(TAG, "closeButton.setOnClickListener - et.getText(): "+et.getText());
            }
        });

        return true;
    }

}
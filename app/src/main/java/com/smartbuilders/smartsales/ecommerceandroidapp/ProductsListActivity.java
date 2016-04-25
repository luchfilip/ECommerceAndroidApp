package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.ProductRecyclerViewAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.SearchResultAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.ProductDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.ProductSubCategoryDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductSubCategory;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

public class ProductsListActivity extends AppCompatActivity {

    private static final String TAG = ProductsListActivity.class.getSimpleName();
    public static final String KEY_PRODUCT = "key_product";
    public static final String KEY_CURRENT_USER = "KEY_CURRENT_USER";
    private static final String STATE_CURRENT_USER = "state_current_user";
    public static final String KEY_PRODUCT_CATEGORY_ID = "KEY_PRODUCT_CATEGORY_ID";
    public static final String KEY_PRODUCT_SUBCATEGORY_ID = "KEY_PRODUCT_SUBCATEGORY_ID";
    public static final String KEY_PRODUCT_BRAND_ID = "KEY_PRODUCT_BRAND_ID";
    public static final String KEY_PRODUCT_NAME = "KEY_PRODUCT_NAME";

    private User mCurrentUser;
    private int productCategoryId;
    private int productSubCategoryId;
    private int productBrandId;
    private String productName;
    private ProductDB productDB;
    private ListView mListView;
    private SearchResultAdapter mSearchResultAdapter;
    private ArrayList<Product> products;
    private ProductRecyclerViewAdapter mProductRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_list);

        if( savedInstanceState != null ) {
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mCurrentUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG, "query: " + query);
        }

        if(getIntent()!=null && getIntent().getExtras()!=null) {
            if(getIntent().getExtras().containsKey(KEY_CURRENT_USER)){
                mCurrentUser = getIntent().getExtras().getParcelable(KEY_CURRENT_USER);

                if(getIntent().getExtras().containsKey(KEY_PRODUCT_CATEGORY_ID)){
                    productCategoryId = getIntent().getExtras().getInt(KEY_PRODUCT_CATEGORY_ID);
                    ProductDB product = new ProductDB(this, mCurrentUser);
                    products = product.getProductsByCategoryId(productCategoryId, null);

                    if(products!=null && !products.isEmpty()){
                        TextView categorySubcategoryResultsTextView = (TextView) findViewById(R.id.category_subcategory_results);
                        Spannable word = new SpannableString(products.get(0).getProductCategory().getDescription() + " ");

                        word.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.product_category)), 0,
                                word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        categorySubcategoryResultsTextView.setText(word);

                        Spannable wordThree = new SpannableString(" ("+(products!=null?products.size():0)+" Resultados) ");
                        categorySubcategoryResultsTextView.append(wordThree);
                    }
                }

                if(getIntent().getExtras().containsKey(KEY_PRODUCT_SUBCATEGORY_ID)){
                    productSubCategoryId = getIntent().getExtras().getInt(KEY_PRODUCT_SUBCATEGORY_ID);
                    ProductDB product = new ProductDB(this, mCurrentUser);
                    products = product.getProductsBySubCategoryId(productSubCategoryId, null);

                    if(products!=null && !products.isEmpty()){
                        TextView categorySubcategoryResultsTextView = (TextView) findViewById(R.id.category_subcategory_results);
                        Spannable word = new SpannableString(products.get(0).getProductCategory().getDescription() + " >> ");

                        word.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.product_category)), 0,
                                word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        categorySubcategoryResultsTextView.setText(word);
                        Spannable wordTwo = new SpannableString(" "+products.get(0).getProductSubCategory().getDescription()+" ");

                        wordTwo.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.product_subcategory)),
                                0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        categorySubcategoryResultsTextView.append(wordTwo);

                        Spannable wordThree = new SpannableString(" ("+(products!=null?products.size():0)+" Resultados) ");
                        categorySubcategoryResultsTextView.append(wordThree);
                    }
                }

                if(getIntent().getExtras().containsKey(KEY_PRODUCT_BRAND_ID)){
                    productBrandId = getIntent().getExtras().getInt(KEY_PRODUCT_BRAND_ID);
                    ProductDB product = new ProductDB(this, mCurrentUser);
                    products = product.getProductsByBrandId(productBrandId);
                    if(products!=null && !products.isEmpty()){
                        TextView categorySubcategoryResultsTextView = (TextView) findViewById(R.id.category_subcategory_results);
                        Spannable wordThree = new SpannableString("("+(products!=null?products.size():0)+" Resultados) ");
                        categorySubcategoryResultsTextView.append(wordThree);
                    }
                }

                if(getIntent().getExtras().containsKey(KEY_PRODUCT_NAME)){
                    productName = getIntent().getExtras().getString(KEY_PRODUCT_NAME);
                    ProductDB product = new ProductDB(this, mCurrentUser);
                    products = product.getProductsByName(productName);

                    if(products!=null && !products.isEmpty()){
                        TextView categorySubcategoryResultsTextView = (TextView) findViewById(R.id.category_subcategory_results);
                        Spannable wordThree = new SpannableString("("+(products!=null?products.size():0)+" Resultados) ");
                        categorySubcategoryResultsTextView.append(wordThree);
                    }
                }
            }
        }

        if(products==null || products.isEmpty()){
            TextView categorySubcategoryResultsTextView = (TextView) findViewById(R.id.category_subcategory_results);
            Spannable word = new SpannableString("No se encontraron productos para mostrar.) ");
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

        productDB = new ProductDB(this, mCurrentUser);

        mSearchResultAdapter = new SearchResultAdapter(this, new ArrayList<Product>());

        mListView = (ListView) findViewById(R.id.search_result_list);
        mListView.setAdapter(mSearchResultAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Product product = (Product) adapterView.getItemAtPosition(position);
                if (product != null) {
                    Intent intent = new Intent(ProductsListActivity.this, ProductsListActivity.class);
                    intent.putExtra(KEY_PRODUCT_SUBCATEGORY_ID, product.getProductSubCategory().getId());
                    intent.putExtra(KEY_CURRENT_USER, mCurrentUser);
                    startActivity(intent);
                    finish();
                }
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
                Intent intent = new Intent(ProductsListActivity.this, ProductsListActivity.class);
                intent.putExtra(KEY_CURRENT_USER, mCurrentUser);
                intent.putExtra(KEY_PRODUCT_NAME, s);
                startActivity(intent);
                finish();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // Some code here
                Log.d(TAG, "onQueryTextChange("+s+")");
                if(s.length()>2){
                    mSearchResultAdapter.setData(productDB.getLightProductsByName(s));
                    mSearchResultAdapter.notifyDataSetChanged();
                }else if (s.isEmpty()){
                    mSearchResultAdapter.setData(new ArrayList<Product>());
                    mSearchResultAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Some code here
                Log.d(TAG, "onMenuItemActionExpand(...)");
                mListView.setVisibility(View.VISIBLE);
                ((RecyclerView) findViewById(R.id.product_list_result)).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.category_subcategory_results)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.filter_linear_layout)).setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Some code here
                Log.d(TAG, "onMenuItemActionCollapse(...)");
                mListView.setVisibility(View.GONE);
                ((RecyclerView) findViewById(R.id.product_list_result)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.category_subcategory_results)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.filter_linear_layout)).setVisibility(View.VISIBLE);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.categories) {
            Intent intent = new Intent(ProductsListActivity.this, FilterOptionsActivity.class);
            intent.putExtra(FilterOptionsActivity.KEY_CURRENT_USER, mCurrentUser);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mCurrentUser);
        super.onSaveInstanceState(outState);
    }
}
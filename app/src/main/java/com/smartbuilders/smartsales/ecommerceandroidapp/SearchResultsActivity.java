package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.SearchResultAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.ProductDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.RecentSearchDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Jesus Sarco 12.05.2016
 */
public class SearchResultsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private User mCurrentUser;
    private ProductDB productDB;
    private SearchResultAdapter mSearchResultAdapter;
    private EditText searchEditText;
    private NavigationView mNavigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        mCurrentUser = Utils.getCurrentUser(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.setCustomToolbarTitle(this, toolbar, true);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        productDB = new ProductDB(this, mCurrentUser);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        ListView mListView = (ListView) findViewById(R.id.search_result_list);
        mSearchResultAdapter = new SearchResultAdapter(this, null, mCurrentUser);
        mListView.setAdapter(mSearchResultAdapter);

        if(findViewById(R.id.search_bar_linear_layout)!=null){
            final Spinner searchByOptionsSpinner = (Spinner) findViewById(R.id.search_by_options_spinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    this, R.array.search_by_options, R.layout.search_by_option_prompt_item);
            adapter.setDropDownViewResource(R.layout.search_by_option_item);
            searchByOptionsSpinner.setAdapter(adapter);
            searchByOptionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedOption = (String) parent.getItemAtPosition(position);
                    if(selectedOption!=null){
                        if(selectedOption.equals(getString(R.string.categories))){
                            startActivity(new Intent(SearchResultsActivity.this, CategoriesListActivity.class));
                        }else if(selectedOption.equals(getString(R.string.brands))){
                            startActivity(new Intent(SearchResultsActivity.this, BrandsListActivity.class));
                        }
                    }
                    searchByOptionsSpinner.setSelection(0);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });

            searchEditText = (EditText) findViewById(R.id.search_product_editText);
            searchEditText.setFocusable(true);
            searchEditText.setFocusableInTouchMode(true);
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(TextUtils.isEmpty(s)){
                        mSearchResultAdapter.setData(null, null, SearchResultsActivity.this);
                    }else{
                        mSearchResultAdapter.setData(null, productDB.getLightProductsByName(s.toString()), SearchResultsActivity.this);
                    }
                    mSearchResultAdapter.notifyDataSetChanged();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if(actionId == EditorInfo.IME_ACTION_SEARCH
                            && !TextUtils.isEmpty(searchEditText.getText())){
                        new RecentSearchDB(SearchResultsActivity.this, mCurrentUser)
                                .insertRecentSearch(searchEditText.getText().toString(), 0, 0);
                        Intent intent = new Intent(SearchResultsActivity.this, ProductsListActivity.class);
                        intent.putExtra(ProductsListActivity.KEY_PRODUCT_NAME, searchEditText.getText().toString());
                        startActivity(intent);
                        return true;
                    }
                    return false;
                }
            });

            findViewById(R.id.image_search_bar_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new RecentSearchDB(SearchResultsActivity.this, mCurrentUser)
                            .insertRecentSearch(searchEditText.getText().toString(), 0, 0);
                    Intent intent = new Intent(SearchResultsActivity.this, ProductsListActivity.class);
                    intent.putExtra(ProductsListActivity.KEY_PRODUCT_NAME, searchEditText.getText().toString());
                    startActivity(intent);
                }
            });
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.delete_all_recent_searches) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.delete_all_recent_searches_question))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            (new RecentSearchDB(SearchResultsActivity.this, mCurrentUser))
                                    .deleteAllRecentSearches();
                            mSearchResultAdapter.setData(null, null, SearchResultsActivity.this);
                            mSearchResultAdapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

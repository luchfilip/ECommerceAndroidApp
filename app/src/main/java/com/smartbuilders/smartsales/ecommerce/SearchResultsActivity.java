package com.smartbuilders.smartsales.ecommerce;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.model.UserProfile;
import com.smartbuilders.smartsales.ecommerce.adapters.SearchResultAdapter;
import com.smartbuilders.smartsales.ecommerce.data.ProductDB;
import com.smartbuilders.smartsales.ecommerce.data.RecentSearchDB;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

/**
 * Jesus Sarco, 12.05.2016
 */
public class SearchResultsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SearchResultAdapter mSearchResultAdapter;
    private ProductDB mProductDB;
    private User mUser;
    private int productId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        mUser = Utils.getCurrentUser(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.setCustomToolbarTitle(this, toolbar, true);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view); //must call super
                Utils.loadNavigationViewBadge(getApplicationContext(), mUser,
                        (NavigationView) findViewById(R.id.nav_view));
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if(navigationView!=null && mUser !=null){
            if(BuildConfig.IS_SALES_FORCE_SYSTEM){
                navigationView.inflateMenu(R.menu.sales_force_system_drawer_menu);
            }else if(mUser.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID){
                navigationView.inflateMenu(R.menu.business_partner_drawer_menu);
            }else if(mUser.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID){
                navigationView.inflateMenu(R.menu.sales_man_drawer_menu);
            }
            navigationView.setNavigationItemSelectedListener(this);
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name))
                    .setText(getString(R.string.welcome_user, mUser.getUserName()));
        }

        mSearchResultAdapter = new SearchResultAdapter(this, null, null, mUser);
        ((ListView) findViewById(R.id.search_result_list)).setAdapter(mSearchResultAdapter);

        if(findViewById(R.id.search_bar_linear_layout)!=null){
            final Spinner searchByOptionsSpinner = (Spinner) findViewById(R.id.search_by_options_spinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    this, R.array.search_by_options, R.layout.spinner_custom_prompt_item);
            if(searchByOptionsSpinner!=null && adapter!=null){
                adapter.setDropDownViewResource(R.layout.spinner_custom_item);
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
                            }else if(selectedOption.equals(getString(R.string.barcode))){
                                Utils.lockScreenOrientation(SearchResultsActivity.this);
                                IntentIntegrator integrator = new IntentIntegrator(SearchResultsActivity.this);
                                integrator.initiateScan();
                            }
                        }
                        searchByOptionsSpinner.setSelection(0);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) { }
                });
            }

            final EditText searchEditText = (EditText) findViewById(R.id.search_product_editText);
            if(searchEditText!=null){
                final ProductDB productDB = new ProductDB(this, mUser);
                searchEditText.setFocusable(true);
                searchEditText.setFocusableInTouchMode(true);
                searchEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(TextUtils.isEmpty(s)){
                            mSearchResultAdapter.setData(null, null);
                        }else{
                            mSearchResultAdapter.setData(s.toString(), productDB.getLightProductsByName(s.toString()));
                        }
                        mSearchResultAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void afterTextChanged(Editable s) { }
                });
                searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if(actionId==EditorInfo.IME_ACTION_SEARCH && searchEditText.getText()!=null){
                            new RecentSearchDB(SearchResultsActivity.this, mUser)
                                    .insertRecentSearch(searchEditText.getText().toString(), 0, 0);
                            startActivity((new Intent(SearchResultsActivity.this, ProductsListActivity.class))
                                    .putExtra(ProductsListActivity.KEY_PRODUCT_NAME, searchEditText.getText().toString()));
                            return true;
                        }
                        return false;
                    }
                });
            }

            View imageSearchBarLayout = findViewById(R.id.image_search_bar_layout);
            if(imageSearchBarLayout!=null){
                imageSearchBarLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(searchEditText!=null && searchEditText.getText()!=null){
                            new RecentSearchDB(SearchResultsActivity.this, mUser)
                                    .insertRecentSearch(searchEditText.getText().toString(), 0, 0);
                            Intent intent = new Intent(SearchResultsActivity.this, ProductsListActivity.class);
                            intent.putExtra(ProductsListActivity.KEY_PRODUCT_NAME, searchEditText.getText().toString());
                            startActivity(intent);
                        }
                    }
                });
            }
        }

        mProductDB = new ProductDB(this, mUser);
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     * <p/>
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see #createPendingResult
     * @see #setResult(int)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IntentIntegrator.REQUEST_CODE){
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (scanResult != null) {
                productId = mProductDB.getProductIdByBarCode(scanResult.getContents());
            }else{
                productId = 0;
                Toast.makeText(this, getString(R.string.no_barcode_captured), Toast.LENGTH_SHORT).show();
            }
            Utils.unlockScreenOrientation(this);
            // else continue with any other code you need in the method
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(productId>0){
            Product product = mProductDB.getProductById(productId);
            if(product!=null){
                DialogProductDetails dialogProductDetails =
                        DialogProductDetails.newInstance(product, mUser);
                dialogProductDetails.show(getSupportFragmentManager(),
                        DialogProductDetails.class.getSimpleName());
            }
            productId = 0;
        }
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer!=null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            (new RecentSearchDB(SearchResultsActivity.this, Utils.getCurrentUser(SearchResultsActivity.this)))
                                    .deleteAllRecentSearches();
                            mSearchResultAdapter.setData(null, null);
                            mSearchResultAdapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

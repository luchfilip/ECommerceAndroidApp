package com.smartbuilders.smartsales.ecommerceandroidapp;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.syncadapter.model.AccountGeneral;
import com.jasgcorp.ids.utils.ApplicationUtilities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.MainActivityRecyclerViewAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.SearchResultAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.MainPageSectionDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.ProductDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.MainPageSection;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Alberto on 22/3/2016.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String KEY_CURRENT_USER = "KEY_CURRENT_USER";
    private static final String STATE_CURRENT_USER = "state_current_user";
    private AccountManager mAccountManager;
    private boolean finishActivityOnResultOperationCanceledException;
    private User mCurrentUser;
    private Account mAccount;
    private NavigationView mNavigationView;
    private RecyclerView mRecyclerView;
//    private ProductDB productDB;
//    private ListView mListView;
//    private SearchResultAdapter mSearchResultAdapter;
//    private boolean isSearchListVisible;
//    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if( savedInstanceState != null ) {
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mCurrentUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
        }

        if(getIntent().getData()!=null){//check if intent is not null
            Uri data = getIntent().getData();//set a variable for the Intent
            String scheme = data.getScheme();//get the scheme (http,https)
            String fullPath = data.getEncodedSchemeSpecificPart();//get the full path -scheme - fragments

            String combine = scheme+":"+fullPath; //combine to get a full URI
            String url = null;//declare variable to hold final URL
            if(combine!=null){//if combine variable is not empty then navigate to that full path
                Log.d(TAG, "combine: "+combine);
                url = combine;
            } else{//else open main page
                Log.e(TAG, "combine is null");
                url = "http://www.example.com";
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        Utils.setCustomToolbarTitle(this, toolbar, mCurrentUser, false);
        setSupportActionBar(toolbar);

        mAccountManager = AccountManager.get(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

//        mListView = (ListView) findViewById(R.id.search_result_list);

        if(findViewById(R.id.search_bar_linear_layout)!=null){
            findViewById(R.id.search_by_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, FilterOptionsActivity.class)
                            .putExtra(FilterOptionsActivity.KEY_CURRENT_USER, mCurrentUser));
                }
            });

            findViewById(R.id.search_product_editText).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, SearchResultsActivity.class)
                            .putExtra(FilterOptionsActivity.KEY_CURRENT_USER, mCurrentUser));
                }
            });

//            searchEditText = (EditText) findViewById(R.id.search_product_editText);
//            searchEditText.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                    if(!isSearchListVisible){
//                        mListView.setVisibility(View.VISIBLE);
//                        findViewById(R.id.main_categories_list).setVisibility(View.GONE);
//                        isSearchListVisible = true;
//                    }
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    if(TextUtils.isEmpty(s)){
//                        isSearchListVisible = false;
//                        mListView.setVisibility(View.GONE);
//                        findViewById(R.id.main_categories_list).setVisibility(View.VISIBLE);
//                    }else{
//                        mSearchResultAdapter.setData(productDB.getLightProductsByName(s.toString()), MainActivity.this);
//                        mSearchResultAdapter.notifyDataSetChanged();
//                    }
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//
//                }
//            });
//            searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//                @Override
//                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                    if(actionId == EditorInfo.IME_ACTION_SEARCH
//                            && !TextUtils.isEmpty(searchEditText.getText())){
//                        Intent intent = new Intent(MainActivity.this, ProductsListActivity.class);
//                        intent.putExtra(ProductsListActivity.KEY_CURRENT_USER, mCurrentUser);
//                        intent.putExtra(ProductsListActivity.KEY_PRODUCT_NAME, searchEditText.getText().toString());
//                        startActivity(intent);
//                        return true;
//                    }
//                    return false;
//                }
//            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putParcelable(STATE_CURRENT_USER, mCurrentUser);
    }

    @Override
    public void onBackPressed() {
//        if(searchEditText!=null && !TextUtils.isEmpty(searchEditText.getText())){
//            searchEditText.setText("");
//        }else{
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        /*SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Intent intent = new Intent(MainActivity.this, ProductsListActivity.class);
                intent.putExtra(ProductsListActivity.KEY_CURRENT_USER, mCurrentUser);
                intent.putExtra(ProductsListActivity.KEY_PRODUCT_NAME, s);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mSearchResultAdapter.setData(productDB.getLightProductsByName(s), MainActivity.this);
                mSearchResultAdapter.notifyDataSetChanged();
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                mListView.setVisibility(View.VISIBLE);
                findViewById(R.id.main_categories_list).setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mListView.setVisibility(View.GONE);
                findViewById(R.id.main_categories_list).setVisibility(View.VISIBLE);
                mSearchResultAdapter.setData(new ArrayList<Product>(), MainActivity.this);
                mSearchResultAdapter.notifyDataSetChanged();
                return true;
            }
        });*/

        // Get the search close button
        //ImageView closeButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
        //closeButton.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        // Some code here
        //        EditText et = (EditText) findViewById(R.id.search_src_text);
        //        Log.d(TAG, "closeButton.setOnClickListener - et.getText(): " + et.getText());
        //    }
        //});
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Utils.navigationItemSelectedBehave(item.getItemId(), this, mCurrentUser);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Add new account to the account manager
     * @param accountType
     * @param authTokenType
     */
    private void addNewAccount(String accountType, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.addAccount(accountType,
                authTokenType, null, null, this, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Bundle bnd = future.getResult();
                    if(bnd!=null && bnd.containsKey(AccountManager.KEY_ACCOUNT_NAME)){
                        String userId = bnd.getBundle(AccountManager.KEY_USERDATA)
                                .getString(AccountGeneral.USERDATA_USER_ID);
                        final Account availableAccounts[] = mAccountManager
                                .getAccountsByType(getString(R.string.authenticator_acount_type));
                        if (availableAccounts!=null && availableAccounts.length>0) {
                            for(Account account : availableAccounts){
                                if(mAccountManager.getUserData(account,
                                        AccountGeneral.USERDATA_USER_ID).equals(userId)){
                                    mCurrentUser = ApplicationUtilities
                                            .getUserByIdFromAccountManager(getApplicationContext(), userId);
                                    break;
                                }
                            }
                        }
                    }
                } catch(OperationCanceledException e){
                    if(finishActivityOnResultOperationCanceledException){
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, null);
    }

    private void loadUserData(Account account){
        mAccount = account;
        mCurrentUser = ApplicationUtilities.getUserByIdFromAccountManager(this,
                mAccountManager.getUserData(account, AccountGeneral.USERDATA_USER_ID));
        try{
            ((TextView) mNavigationView.getHeaderView(0).findViewById(R.id.user_name))
                    .setText(getString(R.string.welcome_user, mCurrentUser.getUserName()));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Account availableAccounts[] = mAccountManager
                .getAccountsByType(getString(R.string.authenticator_acount_type));
        if(availableAccounts!=null && availableAccounts.length>0){
            //Se carga la lista de los usuarios del panel izquierdo
            String[] accountsNames = new String[availableAccounts.length];
            for(int i = 0; i<availableAccounts.length ; i++){
                accountsNames[i] = mAccountManager.getUserData(availableAccounts[i], AccountGeneral.USERDATA_USER_GROUP) +
                        ": " + mAccountManager.getUserData(availableAccounts[i], AccountGeneral.USERDATA_USER_NAME);
            }

            if(mCurrentUser!=null){
                for(int i = 0; i<availableAccounts.length ; i++){
                    if(mAccountManager.getUserData(availableAccounts[i],
                            AccountGeneral.USERDATA_USER_ID).equals(mCurrentUser.getUserId())){
                        loadUserData(availableAccounts[i]);
                        break;
                    }
                }
            }else{
                loadUserData(availableAccounts[0]);
            }
        }

        if(mAccount==null) {
            addNewAccount(getString(R.string.authenticator_acount_type),
                    AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
            finishActivityOnResultOperationCanceledException = true;
        }else{
            finishActivityOnResultOperationCanceledException = false;

            MainPageSectionDB mainPageSectionDB = new MainPageSectionDB(MainActivity.this, mCurrentUser);
            loadMainPage(mainPageSectionDB.getActiveMainPageSections());

//            productDB = new ProductDB(this, mCurrentUser);
//
//            if(mSearchResultAdapter==null){
//                mSearchResultAdapter = new SearchResultAdapter(this, new ArrayList<Product>(), mCurrentUser);
//                mListView.setAdapter(mSearchResultAdapter);
//            }

            File folderThumb = new File(getExternalFilesDir(null)+"/"+mCurrentUser.getUserGroup()
                    +"/"+mCurrentUser.getUserName()+"/Data_In/thumb/");//-->Android/data/package.name/files/...
            // if the directory does not exist, create it
            if (!folderThumb.exists()) {
                try {
                    if (!folderThumb.mkdirs()) {
                        Log.w(TAG, "Failed to create folder: " + folderThumb.getPath() + ".");
                    }
                } catch (SecurityException se) {
                    se.printStackTrace();
                }
            }
            File folderOriginal = new File(getExternalFilesDir(null)+"/"+mCurrentUser.getUserGroup()
                    +"/"+mCurrentUser.getUserName()+"/Data_In/original/");//-->Android/data/package.name/files/...
            // if the directory does not exist, create it
            if (!folderOriginal.exists()) {
                try {
                    if (!folderOriginal.mkdirs()) {
                        Log.w(TAG, "Failed to create folder: " + folderOriginal.getPath() + ".");
                    }
                } catch (SecurityException se) {
                    se.printStackTrace();
                }
            }
        }
    }

    private void loadMainPage(ArrayList<MainPageSection> mainPageSections){
        mRecyclerView = (RecyclerView) findViewById(R.id.main_categories_list);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new MainActivityRecyclerViewAdapter(this, mainPageSections, /*true,*/ mCurrentUser));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mCurrentUser);
        super.onSaveInstanceState(outState);
    }
}

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.MainActivityRecyclerViewAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductCategory;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Alberto on 22/3/2016.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String STATE_CURRENT_USER = "state_current_user";
    private AccountManager mAccountManager;
    private boolean finishActivityOnResultOperationCanceledException;
    private User mCurrentUser;
    private Account mAccount;
    private NavigationView mNavigationView;
    private Button showFiltersButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAccountManager = AccountManager.get(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState != null) {
            mCurrentUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
        }

        showFiltersButton = (Button) findViewById(R.id.show_filters_button);
        showFiltersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FilterOptionsActivity.class));
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.i(TAG, "onSaveInstanceState");
        outState.putParcelable(STATE_CURRENT_USER, mCurrentUser);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_shopping_cart) {
            startActivity(new Intent(this, ShoppingCartActivity.class));
        } else if (id == R.id.nav_whish_list) {
            startActivity(new Intent(this, WishListActivity.class));
        } else if (id == R.id.nav_orders) {
            startActivity(new Intent(this, OrdersListActivity.class));
        } else if (id == R.id.nav_invoices_list) {
            startActivity(new Intent(this, InvoicesListActivity.class));
        } else if (id == R.id.nav_statement_of_account) {
            startActivity(new Intent(this, StatementOfAccountActivity.class));
        } else if (id == R.id.nav_share) {
            try{
                Utils.showPromptShareApp(this);
            }catch(Throwable e){
                e.printStackTrace();
            }
        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_report_error) {

        }

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
                    .setText(mCurrentUser.getUserName());
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

            ArrayList<ProductCategory> categories = new ArrayList<ProductCategory>();
            ProductCategory category = new ProductCategory();
            category.setName("Productos Recientes");
            categories.add(category);

            category = new ProductCategory();
            category.setName("Destacados");
            categories.add(category);

            category = new ProductCategory();
            category.setName("Ofertas");
            categories.add(category);

            category = new ProductCategory();
            category.setName("Lo mas vendido");
            categories.add(category);

            category = new ProductCategory();
            category.setName("Recomendados");
            categories.add(category);

            category = new ProductCategory();
            category.setName("Promociones");
            categories.add(category);

            loadCategoriesList(categories);
        }
    }

    private void loadCategoriesList(ArrayList<ProductCategory> categories){
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.main_categories_list);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new MainActivityRecyclerViewAdapter(categories, true));
    }
}

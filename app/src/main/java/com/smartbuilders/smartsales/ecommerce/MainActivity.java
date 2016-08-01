package com.smartbuilders.smartsales.ecommerce;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.model.UserProfile;
import com.jasgcorp.ids.utils.ApplicationUtilities;
import com.smartbuilders.smartsales.ecommerce.data.ProductDB;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

/**
 * Created by Alberto on 22/3/2016.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final User user = Utils.getCurrentUser(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.setCustomToolbarTitle(this, toolbar, false);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                Utils.loadNavigationViewBadge(getApplicationContext(), user,
                        (NavigationView) findViewById(R.id.nav_view));
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
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

        if(getIntent().getData()!=null){//check if intent is not null
            Uri data = getIntent().getData();//set a variable for the Intent
            //String scheme = data.getScheme();//get the scheme (http,https)
            //String fullPath = data.getEncodedSchemeSpecificPart();//get the full path -scheme - fragments
            //String combine = scheme+":"+fullPath; //combine to get a full URI
            //String url = null;//declare variable to hold final URL
            if(data.getQueryParameter("product")!=null){
                Product product = (new ProductDB(this, user)).getProductByInternalCode(data.getQueryParameter("product").trim());
                if(product!=null){
                    startActivity((new Intent(this, ProductDetailActivity.class))
                            .putExtra(ProductDetailActivity.KEY_PRODUCT_ID, product.getId()));
                }
            }
        }

        if(findViewById(R.id.search_bar_linear_layout)!=null){
            final Spinner searchByOptionsSpinner = (Spinner) findViewById(R.id.search_by_options_spinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    this, R.array.search_by_options, R.layout.spinner_custom_prompt_item);
            if(searchByOptionsSpinner!=null && adapter!=null) {
                adapter.setDropDownViewResource(R.layout.spinner_custom_item);
                searchByOptionsSpinner.setAdapter(adapter);
                searchByOptionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedOption = (String) parent.getItemAtPosition(position);
                        if (selectedOption != null) {
                            if (selectedOption.equals(getString(R.string.categories))) {
                                startActivity(new Intent(MainActivity.this, CategoriesListActivity.class));
                            } else if (selectedOption.equals(getString(R.string.brands))) {
                                startActivity(new Intent(MainActivity.this, BrandsListActivity.class));
                            }
                        }
                        searchByOptionsSpinner.setSelection(0);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
            findViewById(R.id.search_product_editText).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, SearchResultsActivity.class));
                }
            });

            findViewById(R.id.image_search_bar_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, SearchResultsActivity.class));
                }
            });
        }
    }

    @Override
    protected void onStart() {
        findViewById(R.id.badge_ham).setVisibility(View.VISIBLE);
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApplicationUtilities.checkAppVersion(this);
        if(!ApplicationUtilities.checkPlayServices(this)){
            findViewById(R.id.drawer_layout).setVisibility(View.GONE);
        }else if(findViewById(R.id.drawer_layout).getVisibility()==View.GONE){
            findViewById(R.id.drawer_layout).setVisibility(View.VISIBLE);
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
}

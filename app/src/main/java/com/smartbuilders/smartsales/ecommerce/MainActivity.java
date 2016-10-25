package com.smartbuilders.smartsales.ecommerce;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.utils.ApplicationUtilities;
import com.smartbuilders.smartsales.ecommerce.data.ProductDB;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

/**
 * Created by Alberto on 22/3/2016.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ProductDB mProductDB;
    private User mUser;
    private int productId;
    private boolean doubleBackToExitPressedOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUser = Utils.getCurrentUser(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.setCustomToolbarTitle(this, toolbar, false);
        setSupportActionBar(toolbar);

        Utils.inflateNavigationView(this, this, toolbar, mUser);

        if(getIntent()!=null && getIntent().getData()!=null){//check if intent is not null
            Uri data = getIntent().getData();//set a variable for the Intent
            //String scheme = data.getScheme();//get the scheme (http,https)
            //String fullPath = data.getEncodedSchemeSpecificPart();//get the full path -scheme - fragments
            //String combine = scheme+":"+fullPath; //combine to get a full URI
            //String url = null;//declare variable to hold final URL
            if(data.getQueryParameter("product")!=null){
                Product product = (new ProductDB(this, mUser)).getProductByInternalCode(data.getQueryParameter("product").trim());
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
            if(searchByOptionsSpinner!=null) {
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
                            } else if (selectedOption.equals(getString(R.string.barcode))) {
                                Utils.lockScreenOrientation(MainActivity.this);
                                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                                integrator.initiateScan();
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

        ApplicationUtilities.checkAppVersion(this, mUser);
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
    protected void onResume() {
        super.onResume();
        if(!ApplicationUtilities.checkPlayServices(this)){
            findViewById(R.id.drawer_layout).setVisibility(View.GONE);
        }else if(findViewById(R.id.drawer_layout).getVisibility()==View.GONE){
            findViewById(R.id.drawer_layout).setVisibility(View.VISIBLE);
        }
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer!=null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (isTaskRoot()) {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, R.string.click_back_again_to_exit, Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            } else {
                super.onBackPressed();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Utils.navigationItemSelectedBehave(item.getItemId(), this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer!=null){
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }
}

package com.smartbuilders.smartsales.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.smartbuilders.smartsales.ecommerce.utils.Utils;

/**
 * Jesus Sarco, 12.05.2016
 */
public class SalesOrderDetailActivity extends AppCompatActivity
        implements SalesOrderDetailFragment.Callback {

    public static final String KEY_SALES_ORDER_ID = "key_sales_order_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_order_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.setCustomToolbarTitle(this, toolbar, false);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //@Override
    //public boolean onCreateOptionsMenu(Menu menu) {
    //    // Inflate the menu; this adds items to the action bar if it is present.
    //    getMenuInflater().inflate(R.menu.menu_sales_order_detail, menu);
    //    return super.onCreateOptionsMenu(menu);
    //}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        //if (i == R.id.search) {
        //    startActivity(new Intent(this, SearchResultsActivity.class));
        //    return true;
        //} else
        if (i == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean isFragmentMenuVisible() {
        return true;
    }

    @Override
    public void salesOrderDetailLoaded() {
        //do nothing
    }
}
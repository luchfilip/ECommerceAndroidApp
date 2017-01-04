package com.smartbuilders.smartsales.ecommerce;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
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
package com.smartbuilders.smartsales.ecommerce;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.smartbuilders.smartsales.ecommerce.utils.Utils;

/**
 * Alberto Sarco, 26.12.2016
 */
public class BusinessPartnerDetailsActivity extends AppCompatActivity implements BusinessPartnerDetailsFragment.Callback {

    public static final String KEY_BUSINESS_PARTNER_ID = "KEY_BUSINESS_PARTNER_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_partner_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.setCustomToolbarTitle(this, toolbar, true);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onBusinessPartnerSelected(int businessPartnerId) {
        //do nothing
    }
}

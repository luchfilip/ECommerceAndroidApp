package com.smartbuilders.smartsales.ecommerce;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerce.utils.Utils;

/**
 * Jesus Sarco 03.06.2016
 */
public class RegisterBusinessPartnerActivity extends AppCompatActivity
        implements RegisterBusinessPartnerFragment.Callback {

    public static final String KEY_BUSINESS_PARTNER_ID = "KEY_USER_BUSINESS_PARTNER_ID";

    private static final String STATE_BUSINESS_PARTNER_ID = "state_business_partner_id";

    private int mBusinessPartnerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_business_partner);

        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey(STATE_BUSINESS_PARTNER_ID)){
                mBusinessPartnerId = savedInstanceState.getInt(STATE_BUSINESS_PARTNER_ID);
            }
        }else if(getIntent()!=null && getIntent().getExtras()!=null){
            if(getIntent().getExtras().containsKey(KEY_BUSINESS_PARTNER_ID)) {
                mBusinessPartnerId = getIntent().getExtras().getInt(KEY_BUSINESS_PARTNER_ID);
            }
        }

        if (mBusinessPartnerId!=0 && findViewById(R.id.title_textView)!=null) {
            ((TextView) findViewById(R.id.title_textView)).setText(getString(R.string.update_business_partner));
            ((ImageView) findViewById(R.id.toolbar_imageView)).setImageResource(R.drawable.ic_person_white_24dp);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.setCustomToolbarTitle(this, toolbar, true);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_business_partner, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.search) {
            startActivity(new Intent(this, SearchResultsActivity.class));
            return true;
        } else if (i == android.R.id.home) {
            onBackPressed();

            return super.onOptionsItemSelected(item);
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onBusinessPartnerUpdated() {
        //do nothing
    }

    @Override
    public void onBusinessPartnerRegistered() {
        onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_BUSINESS_PARTNER_ID, mBusinessPartnerId);
        super.onSaveInstanceState(outState);
    }
}
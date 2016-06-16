package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

/**
 * Jesus Sarco 03.06.2016
 */
public class RegisterBusinessPartnerActivity extends AppCompatActivity
        implements RegisterBusinessPartnerFragment.Callback {

    public static final String KEY_BUSINESS_PARTNER_ID = "KEY_BUSINESS_PARTNER_ID";
    private static final String STATE_RELOAD_BUSINESS_PARTNER_LIST = "STATE_RELOAD_BUSINESS_PARTNER_LIST";

    private boolean mReloadBusinessPartnersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_business_partner);

        if(savedInstanceState!=null) {
            if(savedInstanceState.containsKey(STATE_RELOAD_BUSINESS_PARTNER_LIST)){
                mReloadBusinessPartnersList = savedInstanceState.getBoolean(STATE_RELOAD_BUSINESS_PARTNER_LIST);
            }
        }

        if(getIntent()!=null && getIntent().getExtras()!=null){
            if(getIntent().getExtras().containsKey(KEY_BUSINESS_PARTNER_ID)) {
                if (findViewById(R.id.title_textView)!=null) {
                    ((TextView) findViewById(R.id.title_textView))
                            .setText(getString(R.string.update_business_partner));
                    ((ImageView) findViewById(R.id.toolbar_imageView))
                            .setImageResource(R.drawable.ic_person_black_24dp);
                }
            }
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
        switch (item.getItemId()) {
            case R.id.search:
                startActivity(new Intent(this, SearchResultsActivity.class));
                return true;
            case android.R.id.home:
                Intent returnIntent = new Intent();
                setResult(mReloadBusinessPartnersList ? RESULT_OK : RESULT_CANCELED, returnIntent);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBusinessPartnerRegistered() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onBusinessPartnerUpdated() {
        mReloadBusinessPartnersList = true;
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(mReloadBusinessPartnersList ? RESULT_OK : RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putBoolean(STATE_RELOAD_BUSINESS_PARTNER_LIST, mReloadBusinessPartnersList);
        super.onSaveInstanceState(outState, outPersistentState);
    }
}
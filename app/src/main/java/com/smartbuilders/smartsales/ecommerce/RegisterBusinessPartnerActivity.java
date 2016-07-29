package com.smartbuilders.smartsales.ecommerce;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.model.UserProfile;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

/**
 * Jesus Sarco 03.06.2016
 */
public class RegisterBusinessPartnerActivity extends AppCompatActivity
        implements RegisterBusinessPartnerFragment.Callback {

    public static final String KEY_BUSINESS_PARTNER_ID = "KEY_BUSINESS_PARTNER_ID";

    private static final String STATE_BUSINESS_PARTNER_ID = "state_business_partner_id";

    private int mBusinessPartnerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_business_partner);

        User user = Utils.getCurrentUser(this);

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
            if(user!=null){
                if(user.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID){
                    ((TextView) findViewById(R.id.title_textView))
                            .setText(getString(R.string.update_business_partner));
                }else if(user.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID){
                    ((TextView) findViewById(R.id.title_textView))
                            .setText(getString(R.string.business_partner_info));
                }
            }
            ((ImageView) findViewById(R.id.toolbar_imageView))
                    .setImageResource(R.drawable.ic_person_white_24dp);
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
                onBackPressed();
            default:
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
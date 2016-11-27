package com.smartbuilders.smartsales.ecommerce;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerce.data.BusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerce.model.BusinessPartner;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.model.UserProfile;

/**
 * Jesus Sarco, 12.05.2016
 */
public class OrderDetailActivity extends AppCompatActivity
        implements OrderDetailFragment.Callback {

    public static final String KEY_ORDER_ID = "key_order_id";

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.setCustomToolbarTitle(this, toolbar, false);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mUser = Utils.getCurrentUser(this);
    }

    @Override
    protected void onStart() {
        if(mUser!=null && (BuildConfig.IS_SALES_FORCE_SYSTEM
                || mUser.getUserProfileId()== UserProfile.SALES_MAN_PROFILE_ID)){
            try {
                BusinessPartner businessPartner = (new BusinessPartnerDB(this, mUser))
                        .getBusinessPartnerById(Utils.getAppCurrentBusinessPartnerId(this, mUser));
                if(businessPartner!=null){
                    ((TextView) findViewById(R.id.business_partner_name)).setText(businessPartner.getName());
                    findViewById(R.id.business_partner_name_container).setVisibility(View.VISIBLE);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        super.onStart();
    }

    //@Override
    //public boolean onCreateOptionsMenu(Menu menu) {
    //    // Inflate the menu; this adds items to the action bar if it is present.
    //    getMenuInflater().inflate(R.menu.menu_order_detail, menu);
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
    public void orderDetailLoaded() {
        //do nothing
    }
}
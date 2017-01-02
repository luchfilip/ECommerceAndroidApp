package com.smartbuilders.smartsales.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerce.data.BusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerce.model.BusinessPartner;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.model.UserProfile;

/**
 * Created by Jesus Sarco, 18.10.2016
 */
public class OrderTrackingDetailActivity extends AppCompatActivity
        implements OrderTrackingDetailFragment.Callback {

    public static final String KEY_ORDER_ID = "KEY_ORDER_ID";

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null) {
            Utils.setCustomActionbarTitle(this, actionBar, false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mUser = Utils.getCurrentUser(this);
    }

    @Override
    protected void onStart() {
        if(mUser!=null && (BuildConfig.IS_SALES_FORCE_SYSTEM
                || mUser.getUserProfileId()==UserProfile.SALES_MAN_PROFILE_ID)){
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_tracking_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.search:
                startActivity(new Intent(this, SearchResultsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void goToOrderDetail(int orderId) {
        startActivity(new Intent(this, OrderDetailActivity.class)
                .putExtra(OrderDetailActivity.KEY_ORDER_ID, orderId));
    }
}

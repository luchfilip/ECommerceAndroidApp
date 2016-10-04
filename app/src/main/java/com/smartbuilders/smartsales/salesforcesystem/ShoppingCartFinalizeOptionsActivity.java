package com.smartbuilders.smartsales.salesforcesystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.ShoppingCartActivity;
import com.smartbuilders.smartsales.ecommerce.model.OrderLine;

import java.util.ArrayList;

/**
 * Created by Jesus Sarco, 03.10.2013
 */
public class ShoppingCartFinalizeOptionsActivity extends AppCompatActivity {

    private static final String STATE_SALES_ORDER_ID = "state_sales_order_id";
    private static final String STATE_BUSINESS_PARTNER_ID = "state_business_partner_id";
    private static final String STATE_ORDER_LINES = "state_order_lines";

    public static final String KEY_SALES_ORDER_ID = "KEY_SALES_ORDER_ID";
    public static final String KEY_BUSINESS_PARTNER_ID = "KEY_BUSINESS_PARTNER_ID";
    public static final String KEY_ORDER_LINES = "KEY_ORDER_LINES";

    private int mSalesOrderId;
    private int mBusinessPartnerId;
    private ArrayList<OrderLine> mOrderLines;
    private boolean mIsShoppingCart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart_finalize_options);

        if (savedInstanceState!=null) {
            if (savedInstanceState.containsKey(STATE_SALES_ORDER_ID)
                    && savedInstanceState.containsKey(STATE_BUSINESS_PARTNER_ID)
                    && savedInstanceState.containsKey(STATE_ORDER_LINES)) {
                mIsShoppingCart = false;
                mSalesOrderId = savedInstanceState.getInt(STATE_SALES_ORDER_ID);
                mBusinessPartnerId = savedInstanceState.getInt(STATE_BUSINESS_PARTNER_ID);
                mOrderLines = savedInstanceState.getParcelableArrayList(STATE_ORDER_LINES);
            }
        } else {
            if (getIntent()!=null && getIntent().getExtras()!=null) {
                if(getIntent().getExtras().containsKey(KEY_SALES_ORDER_ID)
                        && getIntent().getExtras().containsKey(KEY_BUSINESS_PARTNER_ID)
                        && getIntent().getExtras().containsKey(KEY_ORDER_LINES)){
                    mIsShoppingCart = false;
                    mSalesOrderId = getIntent().getExtras().getInt(KEY_SALES_ORDER_ID);
                    mBusinessPartnerId = getIntent().getExtras().getInt(KEY_BUSINESS_PARTNER_ID);
                    mOrderLines = getIntent().getExtras().getParcelableArrayList(KEY_ORDER_LINES);
                }
            }
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goBack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    private void goBack() {
        if (mIsShoppingCart) {
            startActivity(new Intent(this, ShoppingCartActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        } else {
            Intent intent = new Intent(this, ShoppingCartActivity.class);
            intent.putExtra(ShoppingCartActivity.KEY_SALES_ORDER_ID, mSalesOrderId);
            intent.putExtra(ShoppingCartActivity.KEY_BUSINESS_PARTNER_ID, mBusinessPartnerId);
            intent.putExtra(ShoppingCartActivity.KEY_ORDER_LINES, mOrderLines);
            startActivity(intent);
        }
        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (!mIsShoppingCart) {
            outState.putInt(STATE_SALES_ORDER_ID, mSalesOrderId);
            outState.putInt(STATE_BUSINESS_PARTNER_ID, mBusinessPartnerId);
            outState.putParcelableArrayList(STATE_ORDER_LINES, mOrderLines);
        }
        super.onSaveInstanceState(outState);
    }

}

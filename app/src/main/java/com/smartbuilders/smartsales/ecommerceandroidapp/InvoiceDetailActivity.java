package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.jasgcorp.ids.model.User;

import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

public class InvoiceDetailActivity extends AppCompatActivity {

    public static final String KEY_INVOICE_ID = "KEY_INVOICE_ID";
    private User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_detail);

        mCurrentUser = Utils.getCurrentUser(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

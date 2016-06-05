package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

public class BrandsListActivity extends AppCompatActivity {

    private User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brands_list);
        Utils.setCustomActionbarTitle(this, getSupportActionBar(), true);
    }

}

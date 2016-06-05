package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;

public class InvoicesListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoices_list);
        getSupportActionBar().setElevation(0);
    }
}

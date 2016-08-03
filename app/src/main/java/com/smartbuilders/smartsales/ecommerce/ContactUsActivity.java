package com.smartbuilders.smartsales.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.smartbuilders.smartsales.ecommerce.utils.Utils;

public class ContactUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.setCustomToolbarTitle(this, toolbar, true);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactUsEmailIntent = new Intent(Intent.ACTION_SEND);
                contactUsEmailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                // need this to prompts email client only
                contactUsEmailIntent.setType("message/rfc822");
                contactUsEmailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"smartBuilders.ve@gmail.com"});

                startActivity(Intent.createChooser(contactUsEmailIntent, getString(R.string.send_email)));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_us, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int i = menuItem.getItemId();
        if (i == R.id.search) {
            startActivity(new Intent(this, SearchResultsActivity.class));
            return true;
        } else if (i == android.R.id.home) {
            finish();
        }
        return (super.onOptionsItemSelected(menuItem));
    }

}

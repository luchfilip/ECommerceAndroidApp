package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class SubCategoriesListActivity extends AppCompatActivity {

    private final String TAG = SubCategoriesListActivity.class.getSimpleName();
    private final String STATE_SCREEN_ORIENTATION = "state_orientation";
    private int mScreenOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_categories_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        mScreenOrientation = this.getResources().getConfiguration().orientation;
        if(mScreenOrientation==Configuration.ORIENTATION_LANDSCAPE
                && savedInstanceState!=null && savedInstanceState.containsKey(STATE_SCREEN_ORIENTATION)
                && savedInstanceState.getInt(STATE_SCREEN_ORIENTATION)==Configuration.ORIENTATION_PORTRAIT){
            startActivity(new Intent(this, CategoriesListActivity.class));
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                startActivity(new Intent(this, CategoriesListActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, CategoriesListActivity.class));
        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_SCREEN_ORIENTATION, mScreenOrientation);
        super.onSaveInstanceState(outState);
    }
}

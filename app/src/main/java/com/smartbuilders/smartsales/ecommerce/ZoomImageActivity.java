package com.smartbuilders.smartsales.ecommerce;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.smartbuilders.smartsales.ecommerce.utils.Utils;

/**
 * Jesus Sarco, 27.05.2016
 */
public class ZoomImageActivity extends AppCompatActivity {

    public static final String KEY_IMAGE_FILE_NAME = "KEY_IMAGE_FILE_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);

        Utils.setCustomActionbarTitle(this, getSupportActionBar(), true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

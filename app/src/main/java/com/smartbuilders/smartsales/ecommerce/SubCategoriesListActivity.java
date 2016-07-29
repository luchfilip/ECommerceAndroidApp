package com.smartbuilders.smartsales.ecommerce;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerce.data.ProductCategoryDB;
import com.smartbuilders.smartsales.ecommerce.model.ProductCategory;

/**
 * Jesus Sarco, before 30.05.2016
 */
public class SubCategoriesListActivity extends AppCompatActivity {

    public static final String KEY_CATEGORY_ID = "key_category_id";

    private static final String STATE_CATEGORY_ID = "STATE_CATEGORY_ID";

    private int mCategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_categories_list);

        if (savedInstanceState!=null) {
            if(savedInstanceState.containsKey(STATE_CATEGORY_ID)){
                mCategoryId = savedInstanceState.getInt(STATE_CATEGORY_ID);
            }
        } else if(getIntent()!=null && getIntent().getExtras()!=null) {
            if(getIntent().getExtras().containsKey(KEY_CATEGORY_ID)){
                mCategoryId = getIntent().getExtras().getInt(KEY_CATEGORY_ID);
            }
        }


        ProductCategory productCategory = (new ProductCategoryDB(this)).getActiveProductCategoryById(mCategoryId);
         if (productCategory!=null) {
            ((TextView) findViewById(R.id.title_textView))
                    .setText(getString(R.string.category_name_description_detail,
                            productCategory.getName(), productCategory.getDescription()));
        }

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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_CATEGORY_ID, mCategoryId);
        super.onSaveInstanceState(outState);
    }
}
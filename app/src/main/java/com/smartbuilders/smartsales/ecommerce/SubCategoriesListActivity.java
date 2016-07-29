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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_categories_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent()!=null && getIntent().getExtras()!=null){
            if(getIntent().getExtras().containsKey(KEY_CATEGORY_ID)){
                ProductCategory productCategory = (new ProductCategoryDB(this))
                        .getActiveProductCategoryById(getIntent().getExtras().getInt(KEY_CATEGORY_ID));
                if(productCategory!=null){
                    ((TextView) findViewById(R.id.title_textView))
                            .setText(productCategory.getDescription());
                }
            }
        }
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
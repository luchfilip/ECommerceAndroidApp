package com.smartbuilders.smartsales.ecommerce;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.smartbuilders.smartsales.ecommerce.adapters.BrandsListAdapter;

/**
 * Jesus Sarco
 */
public class BrandsListActivity extends AppCompatActivity implements BrandsListFragment.Callback {

    private static final String STATE_CURRENT_FILTER_TEXT = "STATE_CURRENT_FILTER_TEXT";

    private String mCurrentFilterText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brands_list);

        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey(STATE_CURRENT_FILTER_TEXT)){
                mCurrentFilterText = savedInstanceState.getString(STATE_CURRENT_FILTER_TEXT);
            }
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onListLoaded() {
        final ListView listView = (ListView) findViewById(R.id.brands_list);
        final ImageView filterImageView = (ImageView) findViewById(R.id.filter_imageView);
        final EditText filterProduct = (EditText) findViewById(R.id.filter_editText);

        if(filterProduct!=null && filterImageView!=null) {
            filterProduct.setFocusableInTouchMode(true);

            final View.OnClickListener filterImageViewOnClickListener =
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            filterProduct.setText(null);
                        }
                    };
            filterProduct.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s.length()>0){
                        filterImageView.setImageResource(R.drawable.ic_close_black_24dp);
                        filterImageView.setOnClickListener(filterImageViewOnClickListener);
                    }else{
                        filterImageView.setImageResource(R.drawable.ic_filter_list_black_24dp);
                        filterImageView.setOnClickListener(null);
                    }
                    mCurrentFilterText = s.toString();
                    if(listView!=null && (listView.getAdapter() instanceof BrandsListAdapter)) {
                        ((BrandsListAdapter) listView.getAdapter()).filter(mCurrentFilterText);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) { }
            });
            filterProduct.setText(mCurrentFilterText);
            filterProduct.setSelection(filterProduct.length());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_CURRENT_FILTER_TEXT, mCurrentFilterText);
        super.onSaveInstanceState(outState);
    }
}

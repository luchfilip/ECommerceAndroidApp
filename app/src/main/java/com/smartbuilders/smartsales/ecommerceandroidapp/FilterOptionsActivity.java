package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.jasgcorp.ids.model.User;

public class FilterOptionsActivity extends AppCompatActivity {

    public static final String KEY_CURRENT_USER = "KEY_CURRENT_USER";
    public static final String STATE_CURRENT_USER = "state_current_user";
    private User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_options);

        if(getIntent()!=null && getIntent().getExtras()!=null){
            if(getIntent().getExtras().containsKey(KEY_CURRENT_USER)){
                mCurrentUser = getIntent().getExtras().getParcelable(KEY_CURRENT_USER);
            }
        }


        ((Button) findViewById(R.id.search_by_category_button))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(FilterOptionsActivity.this,
                                CategoriesListActivity.class).putExtra(CategoriesListActivity.KEY_CURRENT_USER, mCurrentUser));
                        finish();
                    }
                });

        ((Button) findViewById(R.id.search_by_brand_button))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(FilterOptionsActivity.this,
                                BrandsListActivity.class).putExtra(BrandsListActivity.KEY_CURRENT_USER, mCurrentUser));
                        finish();
                    }
                });
    }

}

package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

public class SubCategoriesListActivity extends AppCompatActivity {

    private final String TAG = SubCategoriesListActivity.class.getSimpleName();
    public static final String KEY_CURRENT_USER = "KEY_CURRENT_USER";
    private static final String STATE_CURRENT_USER = "state_current_user";
    private final String STATE_SCREEN_ORIENTATION = "state_orientation";

    private User mCurrentUser;
    private int mScreenOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_categories_list);

        if( savedInstanceState != null ) {
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mCurrentUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
        }

        if(getIntent()!=null && getIntent().getExtras()!=null){
            if(getIntent().getExtras().containsKey(KEY_CURRENT_USER)){
                mCurrentUser = getIntent().getExtras().getParcelable(KEY_CURRENT_USER);
            }
        }

        mScreenOrientation = this.getResources().getConfiguration().orientation;
        if(mScreenOrientation==Configuration.ORIENTATION_LANDSCAPE
                && savedInstanceState!=null && savedInstanceState.containsKey(STATE_SCREEN_ORIENTATION)
                && savedInstanceState.getInt(STATE_SCREEN_ORIENTATION)==Configuration.ORIENTATION_PORTRAIT){
            Intent intent = new Intent(this, CategoriesListActivity.class);
            intent.putExtra(CategoriesListActivity.KEY_CURRENT_USER, mCurrentUser);
            startActivity(intent);
        }

        Utils.setCustomActionbarTitle(this, getSupportActionBar(), mCurrentUser, true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                goBack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_SCREEN_ORIENTATION, mScreenOrientation);
        outState.putParcelable(STATE_CURRENT_USER, mCurrentUser);
        super.onSaveInstanceState(outState);
    }

    private void goBack(){
        Intent intent = new Intent(this, CategoriesListActivity.class);
        intent.putExtra(CategoriesListActivity.KEY_CURRENT_USER, mCurrentUser);
        startActivity(intent);
        finish();
    }
}
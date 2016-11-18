package com.smartbuilders.smartsales.ecommerce;

import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

/**
 * Created by AlbertoSarco on 24/10/2016.
 */

public class WelcomeScreenSlideActivity extends AppCompatActivity {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 8;
    private static final String STATE_CURRENT_PAGE_SELECTED_POSITION = "STATE_CURRENT_PAGE_SELECTED_POSITION";

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    private boolean doubleBackToExitPressedOnce;
    private TextView skipTextView;
    private int mCurrentPageSelectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen_slide);

        if (savedInstanceState!=null) {
            if (savedInstanceState.containsKey(STATE_CURRENT_PAGE_SELECTED_POSITION)) {
                mCurrentPageSelectedPosition = savedInstanceState.getInt(STATE_CURRENT_PAGE_SELECTED_POSITION);
            }
        }

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(),
                Parameter.isActiveOrderTracking(this, Utils.getCurrentUser(this)));
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentPageSelectedPosition = position;
                // When changing pages, reset the action bar actions since they are dependent
                // on which page is currently active. An alternative approach is to have each
                // fragment expose actions itself (rather than the activity exposing actions),
                // but for simplicity, the activity provides the actions in this sample.
                supportInvalidateOptionsMenu();
                skipTextView.setVisibility((position==mPagerAdapter.getCount()-1) ? View.GONE : View.VISIBLE);
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mPager, true);

        skipTextView = (TextView) findViewById(R.id.skip_textView);
        skipTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        skipTextView.setVisibility((mCurrentPageSelectedPosition==mPagerAdapter.getCount()-1) ? View.GONE : View.VISIBLE);
    }

    /**
     * A simple pager adapter that represents 5 {@link WelcomeScreenSlidePageFragment} objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private boolean mIsActiveOrderTracking;

        public ScreenSlidePagerAdapter(FragmentManager fm, boolean isActiveOrderTracking) {
            super(fm);
            mIsActiveOrderTracking = isActiveOrderTracking;
        }

        @Override
        public Fragment getItem(int position) {
            return WelcomeScreenSlidePageFragment.create(position, mIsActiveOrderTracking);
        }

        @Override
        public int getCount() {
            return mIsActiveOrderTracking ? NUM_PAGES : NUM_PAGES - 1;
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            setResult(RESULT_OK);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.click_back_again_to_exit, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putInt(STATE_CURRENT_PAGE_SELECTED_POSITION, mCurrentPageSelectedPosition);
        super.onSaveInstanceState(outState, outPersistentState);
    }
}

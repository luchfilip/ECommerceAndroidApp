package com.smartbuilders.smartsales.ecommerce.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by stein on 11/6/2016.
 */
public class ViewPager extends android.support.v4.view.ViewPager {

    private boolean mAllowSwap;

    public ViewPager(Context context) {
        super(context);
        mAllowSwap = true;
    }

    public ViewPager(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public void setAllowSwap (boolean allowSwap) {
        this.mAllowSwap = allowSwap;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mAllowSwap && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mAllowSwap && super.onTouchEvent(ev);
    }
}

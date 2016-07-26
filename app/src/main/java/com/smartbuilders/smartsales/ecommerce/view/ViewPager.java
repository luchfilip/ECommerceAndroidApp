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
        if (mAllowSwap) {
            return super.onInterceptTouchEvent(ev);
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mAllowSwap) {
            return super.onTouchEvent(ev);
        }
        return false;
    }
}

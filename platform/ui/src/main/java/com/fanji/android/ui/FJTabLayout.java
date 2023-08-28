package com.fanji.android.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.core.content.ContextCompat;

import com.fanji.android.ui.tablayout.interfaces.IPagerNavigator;

/**
 * created by jiangshide on 2018-08-05.
 * email:18311271399@163.com
 */
public class FJTabLayout extends FrameLayout {

    private IPagerNavigator mNavigator;

    public FJTabLayout(Context context) {
        super(context);
    }

    public FJTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mNavigator != null) {
            mNavigator.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    public void onPageSelected(int position) {
        if (mNavigator != null) {
            mNavigator.onPageSelected(position);
        }
    }

    public void onPageScrollStateChanged(int state) {
        if (mNavigator != null) {
            mNavigator.onPageScrollStateChanged(state);
        }
    }

    public IPagerNavigator getNavigator() {
        return mNavigator;
    }

    public void setNavigator(IPagerNavigator navigator) {
        setNavigator(navigator, R.color.white);
    }

    public void setNavigator(IPagerNavigator navigator, int bgColor) {
        if (mNavigator == navigator) {
            return;
        }
        if (mNavigator != null) {
            mNavigator.onDetachFromMagicIndicator();
        }
        mNavigator = navigator;
        removeAllViews();
        //if (mNavigator instanceof View) {
        //    LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView((View) mNavigator);
        //    ((View) mNavigator).setPadding(15,5,15,5);
        mNavigator.onAttachToMagicIndicator();
        //}
        if(bgColor != 0){
            setBackgroundColor(ContextCompat.getColor(getContext(), bgColor));
        }
    }
}

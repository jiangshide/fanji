package com.fanji.android.ui;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.material.appbar.AppBarLayout;

/**
 * created by jiangshide on 2020/6/4.
 * email:18311271399@163.com
 */
public class FJAppBarLayout extends AppBarLayout {
    public FJAppBarLayout(Context context) {
        super(context);
        init();
    }

    public FJAppBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        //setBackgroundColor(getResources().getColor(SPUtil.getInt(Constant.APP_COLOR_BG,R.color.bg)));
    }
}

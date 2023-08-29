package com.fanji.android.ui;

import android.content.Context;
import android.util.AttributeSet;

/**
 * created by jiangshide on 2020/6/4.
 * email:18311271399@163.com
 */
public class FJSeekBar extends androidx.appcompat.widget.AppCompatSeekBar {

    private int progressDrawable;

    public FJSeekBar(Context context) {
        super(context);
    }

    public FJSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FJSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        progressDrawable = R.drawable.seek_progress;
        setProgressDrawable(getContext().getResources().getDrawable(progressDrawable));
    }

    public void setThumb(int thumb) {
        setThumb(getContext().getResources().getDrawable(thumb));
    }
}

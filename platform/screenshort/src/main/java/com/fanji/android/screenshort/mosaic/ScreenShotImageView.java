package com.fanji.android.screenshort.mosaic;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ScreenShotImageView extends androidx.appcompat.widget.AppCompatImageView {
    private boolean mEnabledHwBitmapsInSwMode;

    public ScreenShotImageView(Context context) {
        super(context);
    }

    public ScreenShotImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setHwBitmapsInSwModeEnabled(boolean enable) {
        mEnabledHwBitmapsInSwMode = enable;
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.setHwBitmapsInSwModeEnabled(mEnabledHwBitmapsInSwMode);
//        try {
//            super.onDraw(canvas);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}

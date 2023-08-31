package com.fanji.android.screenshort.temp;

import android.graphics.Matrix;
import android.view.MotionEvent;
import android.view.View;

public class TextOption implements View.OnTouchListener {

    private View mView;

    private ImgTextView mContainer;

    private float mCenterX, mCenterY;

    private double mRadius, mDegrees;

    private Matrix M = new Matrix();

    public TextOption(ImgTextView container, View view) {
        mView = view;
        mContainer = container;
        mView.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                float x = event.getX();

                float y = event.getY();

                mCenterX = mCenterY = 0;

                float pointX = mView.getX() + x - mContainer.getPivotX();

                float pointY = mView.getY() + y - mContainer.getPivotY();

                mRadius = toLength(0, 0, pointX, pointY);

                mDegrees = toDegrees(pointY, pointX);

                M.setTranslate(pointX - x, pointY - y);

                M.postRotate((float) -toDegrees(pointY, pointX), mCenterX, mCenterY);

                return true;

            case MotionEvent.ACTION_MOVE:

                float[] xy = {event.getX(), event.getY()};

                pointX = mView.getX() + xy[0] - mContainer.getPivotX();

                pointY = mView.getY() + xy[1] - mContainer.getPivotY();

                double radius = toLength(0, 0, pointX, pointY);

                double degrees = toDegrees(pointY, pointX);

                float scale = (float) (radius / mRadius);


                mContainer.addScale(scale);

                mContainer.setRotation((float) (mContainer.getRotation() + degrees - mDegrees));

                mRadius = radius;
                return true;
        }
        return false;
    }

    private static double toDegrees(float v, float v1) {
        return Math.toDegrees(Math.atan2(v, v1));
    }

    private static double toLength(float x1, float y1, float x2, float y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }
}

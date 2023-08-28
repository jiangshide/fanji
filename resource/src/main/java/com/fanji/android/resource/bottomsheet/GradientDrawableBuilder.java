package com.fanji.android.resource.bottomsheet;

import android.graphics.drawable.GradientDrawable;

import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;

/**
 * created by jiangshide on 5/22/21.
 * email:18311271399@163.com
 */
public class GradientDrawableBuilder {

    public static final int ROUNDRECT = 0;
    public static final int OVAL = 1;
    @IntDef({ROUNDRECT, OVAL})
    @interface Shape {}

    public static final int NONE = 0;
    public static final int LINEAR = 1;
    @IntDef ({NONE, LINEAR})
    @interface GradientType {}

    public static final int TOP_BOTTOM = 0;
    public static final int BOTTOM_TOP = 1;
    public static final int LEFT_RIGHT = 2;
    public static final int RIGHT_LEFT = 3;
    @IntDef ({TOP_BOTTOM, BOTTOM_TOP, LEFT_RIGHT, RIGHT_LEFT})
    @interface GradientOrientation {}

    private float mRadius;  // 圆角半径
    private int mShape;     // Oval or RoundRect
    private int mFillColor; // 背景填充色
    private int mStartColor; // 渐变起始色
    private int mEndColor; // 渐变终止色
    private @GradientOrientation int mGradientOrientation; //渐变方向
    private int mGradientType; //渐变类型
    private int mStrokeColor; // stroke 颜色
    private int mStrokeWidth; // stroke 宽度
    private GradientDrawable mGradientDrawable;

    public static GradientDrawableBuilder build() {
        return new GradientDrawableBuilder();
    }

    public GradientDrawableBuilder setGradientOrientation(int gradientOrientation) {
        mGradientOrientation = gradientOrientation;
        return this;
    }

    public GradientDrawableBuilder setGradientType(int gradientType) {
        mGradientType = gradientType;
        return this;
    }

    public GradientDrawableBuilder setStartColor(@ColorInt int startColor) {
        mStartColor = startColor;
        return this;
    }

    public GradientDrawableBuilder setEndColor(@ColorInt int endColor) {
        mEndColor = endColor;
        return this;
    }

    public GradientDrawableBuilder setOval() {
        mShape = 1;
        return this;
    }

    public GradientDrawableBuilder setRoundRect() {
        mShape = 0;
        return this;
    }

    public GradientDrawableBuilder setRadius(float radius) {
        mRadius = radius;
        return this;
    }

    public GradientDrawableBuilder setFillColor(int fillColor) {
        mFillColor = fillColor;
        return this;
    }

    public GradientDrawableBuilder setStrokeColor(int strokeColor) {
        mStrokeColor = strokeColor;
        return this;
    }

    public GradientDrawableBuilder setStrokeWidth(int strokeWidth) {
        mStrokeWidth = strokeWidth;
        return this;
    }

    public GradientDrawable getGradientDrawable() {
        return mGradientDrawable;
    }

    /**
     * 依据 builder 中的参数，创建 GradientDrawable
     */
    public GradientDrawable create() {
        if(mGradientDrawable == null) {
            mGradientDrawable = new GradientDrawable();
        }
        if(mShape == OVAL) {
            mGradientDrawable.setShape(GradientDrawable.OVAL);
        } else {
            mGradientDrawable.setShape(GradientDrawable.RECTANGLE);
        }
        if(mRadius > 0) {
            mGradientDrawable.setCornerRadius(mRadius);
        }
        if(mStrokeWidth > 0 && mStrokeColor != 0) {
            mGradientDrawable.setStroke(mStrokeWidth, mStrokeColor);
        }
        if(mFillColor != 0) {
            mGradientDrawable.setColor(mFillColor);
        }
        if(mGradientType == LINEAR) {
            mGradientDrawable.mutate();
            mGradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            switch (mGradientOrientation) {
                case TOP_BOTTOM:
                    mGradientDrawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
                    break;
                case BOTTOM_TOP:
                    mGradientDrawable.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
                    break;
                case LEFT_RIGHT:
                    mGradientDrawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
                    break;
                case RIGHT_LEFT:
                    mGradientDrawable.setOrientation(GradientDrawable.Orientation.RIGHT_LEFT);
                    break;
                default:
                    break;
            }
            mGradientDrawable.setColors(new int[] {mStartColor, mEndColor});
        }
        return mGradientDrawable;
    }
}

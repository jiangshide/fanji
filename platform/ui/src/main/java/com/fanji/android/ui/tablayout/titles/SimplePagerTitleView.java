package com.fanji.android.ui.tablayout.titles;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.Gravity;

import androidx.appcompat.widget.AppCompatTextView;

import com.fanji.android.util.ScreenUtil;
import com.fanji.android.ui.tablayout.interfaces.IMeasurablePagerTitleView;

/**
 * created by jiangshide on 2019-08-05.
 * email:18311271399@163.com
 */
public class SimplePagerTitleView extends AppCompatTextView implements IMeasurablePagerTitleView {
    protected int mSelectedColor;
    protected int mNormalColor;
    protected float mSelectedSize;
    protected float mNormalSize;

    public SimplePagerTitleView(Context context) {
        super(context, null);
        init(context);
    }

    private void init(Context context) {
        setGravity(Gravity.CENTER);
//        setWidth(ScreenUtil.dip2px(context, 60));
        int padding = ScreenUtil.dip2px(context, 10);
        setPadding(padding, 0, padding, 0);
        setSingleLine();
        setEllipsize(TextUtils.TruncateAt.END);
    }

    @Override
    public void onSelected(int index, int totalCount) {
        setTextColor(mSelectedColor);
        setTextSize(mSelectedSize);
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        setTextColor(mNormalColor);
        setTextSize(mNormalSize);
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
    }

    @Override
    public int getContentLeft() {
        Rect bound = new Rect();
        String longestString = "";
        if (getText().toString().contains("\n")) {
            String[] brokenStrings = getText().toString().split("\\n");
            for (String each : brokenStrings) {
                if (each.length() > longestString.length()) longestString = each;
            }
        } else {
            longestString = getText().toString();
        }
        getPaint().getTextBounds(longestString, 0, longestString.length(), bound);
        int contentWidth = bound.width();
        return getLeft() + getWidth() / 2 - contentWidth / 2;
    }

    @Override
    public int getContentTop() {
        Paint.FontMetrics metrics = getPaint().getFontMetrics();
        float contentHeight = metrics.bottom - metrics.top;
        return (int) (getHeight() / 2 - contentHeight / 2);
    }

    @Override
    public int getContentRight() {
        Rect bound = new Rect();
        String longestString = "";
        if (getText().toString().contains("\n")) {
            String[] brokenStrings = getText().toString().split("\\n");
            for (String each : brokenStrings) {
                if (each.length() > longestString.length()) longestString = each;
            }
        } else {
            longestString = getText().toString();
        }
        getPaint().getTextBounds(longestString, 0, longestString.length(), bound);
        int contentWidth = bound.width();
        return getLeft() + getWidth() / 2 + contentWidth / 2;
    }

    @Override
    public int getContentBottom() {
        Paint.FontMetrics metrics = getPaint().getFontMetrics();
        float contentHeight = metrics.bottom - metrics.top;
        return (int) (getHeight() / 2 + contentHeight / 2);
    }

    //public int getSelectedColor() {
    //    return mSelectedColor;
    //}

    public SimplePagerTitleView setSelectedColor(int selectedColor) {
        mSelectedColor = selectedColor;
        return this;
    }

    //public int getNormalColor() {
    //    return mNormalColor;
    //}

    public SimplePagerTitleView setNormalColor(int normalColor) {
        mNormalColor = normalColor;
        return this;
    }

    public SimplePagerTitleView setSelectedSize(int size){
        mSelectedSize = size;
        return this;
    }

    public SimplePagerTitleView setNormalSize(int size){
        mNormalSize = size;
        return this;
    }
}

package com.fanji.android.ui.navigation.item;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.fanji.android.ui.FJTextView;
import com.fanji.android.ui.R;
import com.fanji.android.ui.navigation.internal.RoundMessageView;

/**
 * created by jiangshide on 4/10/21.
 * email:18311271399@163.com
 */
public class SpecialTab extends BaseTabItem {

    private ImageView mIcon;
    private final FJTextView mTitle;
    private final RoundMessageView mMessages;

    private Drawable mDefaultDrawable;
    private Drawable mCheckedDrawable;
    @ColorInt
    private int mDefaultTextColor;
    @ColorInt
    private int mCheckedTextColor;
    @ColorInt
    private int mTextEndColor;
    private boolean mChecked;

    public SpecialTab(Context context) {
        this(context, null);
    }

    public SpecialTab(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpecialTab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.special_tab, this, true);

        mIcon = findViewById(R.id.icon);
        mTitle = findViewById(R.id.title);
        mMessages = findViewById(R.id.messages);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        View view = getChildAt(0);
        if (view != null) {
            view.setOnClickListener(l);
        }
    }

    /**
     * 方便初始化的方法
     *
     * @param drawableRes        默认状态的图标
     * @param checkedDrawableRes 选中状态的图标
     * @param title              标题
     */
    public void initialize(@DrawableRes int drawableRes, @DrawableRes int checkedDrawableRes, String title) {
        mDefaultDrawable = ContextCompat.getDrawable(getContext(), drawableRes);
        mCheckedDrawable = ContextCompat.getDrawable(getContext(), checkedDrawableRes);
        mTitle.setText(title);
    }

    @Override
    public void setChecked(boolean checked) {
        if (checked) {
            mIcon.setImageDrawable(mCheckedDrawable);
            if (mTextEndColor != 0) {
                mTitle.setGradientText(mCheckedTextColor, mTextEndColor);
            } else {
                mTitle.setGradientText(mCheckedTextColor, mCheckedTextColor);
            }
        } else {
            mIcon.setImageDrawable(mDefaultDrawable);
            mTitle.setGradientText(mDefaultTextColor, mDefaultTextColor);
        }
        mChecked = checked;
    }

    @Override
    public void setMessageNumber(int number) {
        mMessages.setMessageNumber(number);
    }

    @Override
    public void setHasMessage(boolean hasMessage) {
        mMessages.setHasMessage(hasMessage);
    }

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public void setDefaultDrawable(Drawable drawable) {
        mDefaultDrawable = drawable;
        if (!mChecked) {
            mIcon.setImageDrawable(drawable);
        }
    }

    @Override
    public void setSelectedDrawable(Drawable drawable) {
        mCheckedDrawable = drawable;
        if (mChecked) {
            mIcon.setImageDrawable(drawable);
        }
    }

    @Override
    public String getTitle() {
        return mTitle.getText().toString();
    }

    public void setTextDefaultColor(int color) {
        mDefaultTextColor = ContextCompat.getColor(getContext(), color);
    }

    public void setTextCheckedColor(int color) {
        mCheckedTextColor = ContextCompat.getColor(getContext(), color);
    }

    public void setTextEndColor(int color) {
        mTextEndColor = ContextCompat.getColor(getContext(), color);
    }
}

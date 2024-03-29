package com.fanji.android.ui.navigation.item;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.fanji.android.ui.FJImageView;
import com.fanji.android.ui.R;
import com.fanji.android.ui.anim.Anim;
import com.fanji.android.ui.navigation.internal.RoundMessageView;

/**
 * created by jiangshide on 4/10/21.
 * email:18311271399@163.com
 */
public class SpecialTabRound extends BaseTabItem {

    private FJImageView mIcon;
    private final TextView mTitle;
    private final RoundMessageView mMessages;

    private int mDefaultRes;
    private int mCheckedRes;

    private Drawable mDefaultDrawable;
    private Drawable mCheckedDrawable;

    private int mDefaultTextColor = R.color.neutralLight;
    private int mCheckedTextColor = R.color.theme;

    private String mSelectUrl;
    private String mSelectedUrl;

    private boolean mChecked;

    public SpecialTabRound(Context context) {
        this(context, null);
    }

    public SpecialTabRound(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpecialTabRound(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.special_tab_round, this, true);

        mIcon = findViewById(R.id.icon);
        mTitle = findViewById(R.id.title);
        mMessages = findViewById(R.id.messages);
    }

    /**
     * 方便初始化的方法
     *
     * @param drawableRes        默认状态的图标
     * @param checkedDrawableRes 选中状态的图标
     * @param title              标题
     */
    public void initialize(@DrawableRes int drawableRes, @DrawableRes int checkedDrawableRes, String title) {
        this.mDefaultRes = drawableRes;
        this.mCheckedRes = checkedDrawableRes;
        mDefaultDrawable = ContextCompat.getDrawable(getContext(), drawableRes);
        mCheckedDrawable = ContextCompat.getDrawable(getContext(), checkedDrawableRes);
        if (!title.isEmpty()) {
            mTitle.setText(title);
        }
    }

    public SpecialTabRound setUrl(String selectUrl, String selectedUrl) {
        this.mSelectUrl = selectUrl;
        this.mSelectedUrl = selectedUrl;
        mIcon.loadCircle(mSelectedUrl, mCheckedRes);
        mIcon.loadCircle(mSelectUrl, mDefaultRes);
        return this;
    }

    public SpecialTabRound startAnim(boolean isStart) {
        if (isStart) {
            Anim.anim(mIcon, R.anim.circulate);
        } else {
            mIcon.clearAnimation();
        }
        return this;
    }

    @Override
    public void setChecked(boolean checked) {
        if (checked) {
            if (!TextUtils.isEmpty(mSelectedUrl)) {
                mIcon.loadCircle(mSelectedUrl, mCheckedRes);
            } else {
                mIcon.setImageDrawable(mCheckedDrawable);
            }
            mTitle.setTextColor(ContextCompat.getColor(getContext(), mCheckedTextColor));
        } else {
            if (!TextUtils.isEmpty(mSelectUrl)) {
                mIcon.loadCircle(mSelectUrl, mDefaultRes);
            } else {
                mIcon.setImageDrawable(mDefaultDrawable);
            }
            mTitle.setTextColor(ContextCompat.getColor(getContext(), mDefaultTextColor));
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

    public void setTextDefaultColor(@ColorInt int color) {
        mDefaultTextColor = color;
    }

    public void setTextCheckedColor(@ColorInt int color) {
        mCheckedTextColor = color;
    }
}

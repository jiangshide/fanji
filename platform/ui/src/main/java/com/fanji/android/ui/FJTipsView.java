package com.fanji.android.ui;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * created by jiangshide on 2020-01-22.
 * email:18311271399@163.com
 */
public class FJTipsView extends LinearLayout {

    private RelativeLayout tipsL;
    private FJLoading tipsLoading;
    private LinearLayout tipsFalseL;
    private ImageView tipsImg;
    private TextView tipsDes;
    private FJButton tipsRetry;
    private OnRetryListener mOnTipsListener;

    public FJTipsView(Context context) {
        super(context);
        init();
    }

    public FJTipsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FJTipsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = getView(R.layout.default_tips);
        tipsL = view.findViewById(R.id.tipsL);
        tipsLoading = view.findViewById(R.id.tipsLoading);
        tipsFalseL = view.findViewById(R.id.tipsFalseL);
        tipsImg = view.findViewById(R.id.tipsImg);
        tipsDes = view.findViewById(R.id.tipsDes);
        tipsRetry = view.findViewById(R.id.tipsRetry);
        tipsRetry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnTipsListener != null) {
                    mOnTipsListener.onRetry(v);
                }
            }
        });
        addView(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        tipsL.setVisibility(View.GONE);
    }

    public void setVisibility(boolean visibility) {
        if (tipsL != null) {
            tipsL.setVisibility(visibility ? View.VISIBLE : View.GONE);
        }
    }

    public FJTipsView setTipsImg(int res) {
        if (tipsImg != null) {
            tipsImg.setImageResource(res);
        }
        return this;
    }

    public FJTipsView setTipsDes(int res) {
        return setTipsDes(getContext().getResources().getString(res));
    }

    public FJTipsView setTipsDes(String tips) {
        if (tipsDes != null && !TextUtils.isEmpty(tips)) {
            tipsDes.setText(tips);
        }
        return this;
    }

    public FJTipsView setTipsSize(int size) {
        if (tipsDes != null) {
            tipsDes.setTextSize(size);
        }
        return this;
    }

    public FJTipsView setTipsColor(int color) {
        if (tipsDes != null) {
            tipsDes.setTextColor(getContext().getResources().getColor(color));
        }
        return this;
    }

    public FJTipsView setBtnTips(String tips) {
        if (tipsRetry != null) {
            if (!TextUtils.isEmpty(tips)) {
                tipsRetry.setText(tips);
            }
        }
        return this;
    }

    public FJTipsView setListener(OnRetryListener listener) {
        this.mOnTipsListener = listener;
        return this;
    }

    public FJTipsView setStatus(boolean isTipsL, boolean isTipsLoadingL, boolean isTipsFalseL) {
        tipsL.setVisibility(isTipsL ? View.VISIBLE : View.GONE);
        tipsLoading.setVisibility(isTipsLoadingL ? View.VISIBLE : View.GONE);
        tipsFalseL.setVisibility(isTipsFalseL ? View.VISIBLE : View.GONE);
        return this;
    }

    private View getView(int layout) {
        return LayoutInflater.from(getContext()).inflate(layout, null);
    }

    public interface OnRetryListener {
        void onRetry(View view);
    }
}

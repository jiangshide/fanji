package com.fanji.android.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.fanji.android.util.ScreenUtil;

/**
 * @author: jiangshide
 * @date: 2023/9/28
 * @email: 18311271399@163.com
 * @description:
 */
public class FJNumberSelectView extends View {
    private int mBackgroundColorNormal;
    private int mBackgroundColorSelect;
    private int mTextColor;
    private int mStrokeColor;
    private float mStrokeWidth;
    private float mSolidRadius;//实心圆半径
    private float mRingRadius;//圆环半径
    private float mTextSize;

    private int mCenterX;//圆心x坐标
    private int mCenterY;//圆心y坐标

    private Paint mStrokePaint;//圆环画笔
    private Paint mSolidPaint;//背景填充画笔
    private Paint mTextPaint;//文字画笔

    private String text;//要画的数字
    private boolean isSelected;//是否已经被选上

    private OnOnStateChangeListener listener;

    public void setOnStateChangeListener(OnOnStateChangeListener listener) {
        this.listener = listener;
    }

    public interface OnOnStateChangeListener {
        void onClick(boolean isSelected);
    }


    public FJNumberSelectView(Context context) {
        this(context, null);
    }

    public FJNumberSelectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FJNumberSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NumberSelectView);
        mBackgroundColorNormal = typedArray.getColor(R.styleable.NumberSelectView_backgroundColorNormal, Color.parseColor("#33000000"));
        mBackgroundColorSelect = typedArray.getColor(R.styleable.NumberSelectView_getBackgroundColorSelect, Color.parseColor("#ff5f62"));
        mTextColor = typedArray.getColor(R.styleable.NumberSelectView_textColor, Color.parseColor("#FFFFFF"));
        mStrokeColor = typedArray.getColor(R.styleable.NumberSelectView_strokeColor, Color.parseColor("#66FFFFFF"));
        mStrokeWidth = typedArray.getDimension(R.styleable.NumberSelectView_strokeWidth, ScreenUtil.dip2px(context, 1.0f));
        mSolidRadius = typedArray.getDimension(R.styleable.NumberSelectView_solidRadius, ScreenUtil.dip2px(context, 12.0f));
        mTextSize = typedArray.getDimension(R.styleable.NumberSelectView_textSize, ScreenUtil.sp2px(context, 10.0f));
        text = typedArray.getString(R.styleable.NumberSelectView_text);
        typedArray.recycle();//回收很重要
        init();
    }

    private void init() {
        if (TextUtils.isEmpty(text)) {
            text = "1";
        }

        mRingRadius = mSolidRadius + mStrokeWidth / 2;
        setClickable(true);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
                if (listener != null) {
                    listener.onClick(isSelected);
                }
            }
        });

        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setColor(mStrokeColor);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(mStrokeWidth);

        mSolidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSolidPaint.setColor(mBackgroundColorNormal);
        mSolidPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawCircle(canvas);
        drawRing(canvas);
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(text, 0, text.length(), bounds);
        float x = (getMeasuredWidth() - bounds.width()) / 2;
        float y = (getMeasuredHeight() + bounds.height()) / 2;
        if (isSelected) {
            canvas.drawText(text, x, y, mTextPaint);
        } else {
            canvas.drawText("", x, y, mTextPaint);
        }
    }

    private void drawRing(Canvas canvas) {
        RectF rectF = new RectF();
        rectF.top = mCenterY - mRingRadius;
        rectF.bottom = mCenterY + mRingRadius;
        rectF.left = mCenterX - mRingRadius;
        rectF.right = mCenterX + mRingRadius;
        canvas.drawArc(rectF, 0, 360, false, mStrokePaint);
    }

    private void drawCircle(Canvas canvas) {
        if (!isSelected) {
            canvas.drawCircle(mCenterX, mCenterY, mSolidRadius, mSolidPaint);
        } else {
            mSolidPaint.setColor(mBackgroundColorSelect);
            canvas.drawCircle(mCenterX, mCenterY, mSolidRadius, mSolidPaint);
            mSolidPaint.setColor(mBackgroundColorNormal);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int exceptWidth = (int) ((mSolidRadius + mStrokeWidth) * 2) + getPaddingLeft() + getPaddingRight();
        int exceptHight = (int) ((mSolidRadius + mStrokeWidth) * 2) + getPaddingTop() + getPaddingBottom();
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(exceptWidth, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(exceptHight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mCenterX = w / 2;
        mCenterY = h / 2;
    }

    private void toggle() {
        isSelected = !isSelected;
//        invalidate();
    }

    public void setNum(int num, boolean selected) {
        setViewText(selected ? num + "" : "", selected);
    }

    public void setViewText(String text, boolean isViewClick) {
        this.text = text;
        if (!isViewClick) {
            if (TextUtils.isEmpty(text)) {
                isSelected = false;
            } else {
                isSelected = true;
            }
        }
        invalidate();
    }

    public String getViewText() {
        return text;
    }

    public boolean isViewSelected() {
        return isSelected;
    }
}

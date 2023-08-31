package com.fanji.android.screenshort.temp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanji.android.screenshort.R;
import com.fanji.android.util.LogUtil;

public class ImgTextView extends ViewGroup implements IImg, View.OnClickListener {

    private View mContentView;

    private float mScale = 1f;

    private ImgHelper<ImgTextView> mStickerHelper;

    private ImageView mRemoveView, mAdjustView;

    private Paint PAINT;

    private Matrix mMatrix = new Matrix();

    private RectF mFrame = new RectF();

    private Rect mTempFrame = new Rect();

    private float mX, mY;

    private static final Matrix M = new Matrix();

    private static final int ANCHOR_SIZE = 48;

    private static final int ANCHOR_SIZE_HALF = ANCHOR_SIZE >> 1;

    private static final float STROKE_WIDTH = 3f;

    private EditText mTextView;

    private static float mBaseTextSize = -1f;

    private static final int PADDING = 26;

    private static final float TEXT_SIZE_SP = 24f;

    {
        PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
        PAINT.setColor(Color.WHITE);
        PAINT.setStyle(Paint.Style.STROKE);
        PAINT.setStrokeWidth(STROKE_WIDTH);
    }

    public ImgTextView(Context context) {
        this(context, null, 0);
    }

    public ImgTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImgTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onInitialize(context);
    }

    public void onInitialize(Context context) {
        if (mBaseTextSize <= 0) {
            mBaseTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    TEXT_SIZE_SP, context.getResources().getDisplayMetrics());
        }

        setBackgroundColor(Color.TRANSPARENT);

        mContentView = onCreateContentView(context);
        addView(mContentView, getContentLayoutParams());

        mRemoveView = new ImageView(context);
        mRemoveView.setScaleType(ImageView.ScaleType.FIT_XY);
        mRemoveView.setImageResource(R.mipmap.image_ic_delete);
        addView(mRemoveView, getAnchorLayoutParams());
        mRemoveView.setOnClickListener(this);

        mAdjustView = new ImageView(context);
        mAdjustView.setScaleType(ImageView.ScaleType.FIT_XY);
        mAdjustView.setImageResource(R.mipmap.image_ic_adjust);
        addView(mAdjustView, getAnchorLayoutParams());

        new TextOption(this, mAdjustView);

        mStickerHelper = new ImgHelper<>(this);
    }

    public View onCreateContentView(Context context) {
        mTextView = new EditText(context);
        mTextView.setFocusableInTouchMode(true);
        mTextView.setFocusable(true);
        mTextView.requestFocus();
        mTextView.setBackground(null);
        mTextView.setTextSize(mBaseTextSize);
        mTextView.setPadding(PADDING, PADDING, PADDING, PADDING);
        mTextView.setTextColor(Color.WHITE);
        mTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mTextView.setFocusableInTouchMode(true);
                mTextView.setFocusable(true);
                mTextView.requestFocus();
            }
        });
        return mTextView;
    }

    public ImgTextView setText(String str) {
        if (mTextView != null) {
            mTextView.setText(str);
        }
        return this;
    }

    public ImgTextView setTextColor(int color) {
        if (mTextView != null) {
            mTextView.setTextColor(color);
        }
        return this;
    }

    public TextView getText() {
        return mTextView;
    }

    @Override
    public float getScale() {
        return mScale;
    }

    @Override
    public void setScale(float scale) {
        mScale = scale;

        mContentView.setScaleX(mScale);
        mContentView.setScaleY(mScale);

        int pivotX = (getLeft() + getRight()) >> 1;
        int pivotY = (getTop() + getBottom()) >> 1;

        mFrame.set(pivotX, pivotY, pivotX, pivotY);
        mFrame.inset(-(mContentView.getMeasuredWidth() >> 1), -(mContentView.getMeasuredHeight() >> 1));

        mMatrix.setScale(mScale, mScale, mFrame.centerX(), mFrame.centerY());
        mMatrix.mapRect(mFrame);

        mFrame.round(mTempFrame);

        layout(mTempFrame.left, mTempFrame.top, mTempFrame.right, mTempFrame.bottom);
    }

    @Override
    public void addScale(float scale) {
        setScale(getScale() * scale);
    }

    private LayoutParams getContentLayoutParams() {
        return new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
    }

    private LayoutParams getAnchorLayoutParams() {
        return new LayoutParams(ANCHOR_SIZE, ANCHOR_SIZE);
    }

    @Override
    public void draw(Canvas canvas) {
        if (isShowing()) {
            canvas.drawRect(ANCHOR_SIZE_HALF, ANCHOR_SIZE_HALF,
                    getWidth() - ANCHOR_SIZE_HALF,
                    getHeight() - ANCHOR_SIZE_HALF, PAINT);
        }
        super.draw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();

        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                child.measure(widthMeasureSpec, heightMeasureSpec);

                maxWidth = Math.round(Math.max(maxWidth, child.getMeasuredWidth() * child.getScaleX()));
                maxHeight = Math.round(Math.max(maxHeight, child.getMeasuredHeight() * child.getScaleY()));

                childState = combineMeasuredStates(childState, child.getMeasuredState());
            }
        }

        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec, childState << MEASURED_HEIGHT_STATE_SHIFT));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        mFrame.set(left, top, right, bottom);

        int count = getChildCount();
        if (count == 0) {
            return;
        }

        mRemoveView.layout(0, 0, mRemoveView.getMeasuredWidth(), mRemoveView.getMeasuredHeight());
        mAdjustView.layout(
                right - left - mAdjustView.getMeasuredWidth(),
                bottom - top - mAdjustView.getMeasuredHeight(),
                right - left, bottom - top
        );

        int centerX = (right - left) >> 1, centerY = (bottom - top) >> 1;
        int hw = mContentView.getMeasuredWidth() >> 1;
        int hh = mContentView.getMeasuredHeight() >> 1;

        mContentView.layout(centerX - hw, centerY - hh, centerX + hw, centerY + hh);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return isShowing() && super.drawChild(canvas, child, drawingTime);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isShowing() && ev.getAction() == MotionEvent.ACTION_DOWN) {
            show();
            return true;
        }
        return isShowing() && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        boolean handled = onTouch(this, event);
        float x = getX();
        float y = getY();
        LogUtil.e("----onTouchEvent~x:", x, " | y:", y);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                onContentTap();
                break;
        }

        return handled | super.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        if (v == mRemoveView) {
            onRemove();
        }
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mX = event.getX();
                mY = event.getY();
                M.reset();
                M.setRotate(v.getRotation());
                return true;
            case MotionEvent.ACTION_MOVE:
                float[] dxy = {event.getX() - mX, event.getY() - mY};
                M.mapPoints(dxy);
                v.setTranslationX(getTranslationX() + dxy[0]);
                v.setTranslationY(getTranslationY() + dxy[1]);
                return true;
        }
        return false;
    }

    public void onRemove() {
        mStickerHelper.remove();
    }

    public void onContentTap() {
        //todo:点击时回调重显文本
        LogUtil.e("------mText:", mTextView.getText());
    }

    @Override
    public boolean show() {
        return mStickerHelper.show();
    }

    @Override
    public boolean remove() {
        return mStickerHelper.remove();
    }

    @Override
    public boolean dismiss() {
        return mStickerHelper.dismiss();
    }

    @Override
    public boolean isShowing() {
        return mStickerHelper.isShowing();
    }

    @Override
    public RectF getFrame() {
        return mStickerHelper.getFrame();
    }

    @Override
    public void onSticker(Canvas canvas) {
        canvas.translate(mContentView.getX(), mContentView.getY());
        mContentView.draw(canvas);
    }

    @Override
    public void registerCallback(ImgPortrait.Callback callback) {
        mStickerHelper.registerCallback(callback);
    }

    @Override
    public void unregisterCallback(Callback callback) {
        mStickerHelper.unregisterCallback(callback);
    }
}

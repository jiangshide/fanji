package com.fanji.android.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

/**
 * created by jiangshide on 2020/4/25.
 * email:18311271399@163.com
 */
public class FJProgressView extends View {

  private static final float STROKE_SIZE = 4;
  private static final float START_ANGLE = 135;
  private static final float MIDDLE_ANGLE = 270;
  private static final float GAP_ANGLE =90;
  private static final float FULLPROGRESS_ANGLE = 360 - GAP_ANGLE;

  private final RectF mBounds = new RectF();
  private final Paint mPaint = new Paint();

  private float mStrokeSize;
  private float mStartAngle;
  private float mMiddleAngle;
  private float mGapAngle;
  private float mFullProgressAngle;
  private int mBackgroundColor;
  private int mForegroundColor;

  private int mProgress = 0;
  private int mMax = 100;
  private float mMorph;

  public FJProgressView(Context context) {
    this(context, null, 0);
  }

  public FJProgressView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public FJProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    if (attrs != null) {
      TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundProgress);
      mStrokeSize = typedArray.getInteger(R.styleable.RoundProgress_strokeSize, 10);
      mForegroundColor =
          typedArray.getColor(R.styleable.RoundProgress_foregroundColor, Color.BLACK);
      mBackgroundColor =
          typedArray.getColor(R.styleable.RoundProgress_backgroundColor, Color.WHITE);
      mStartAngle = typedArray.getFloat(R.styleable.RoundProgress_strokeSize, START_ANGLE);
      mMiddleAngle = typedArray.getFloat(R.styleable.RoundProgress_middleAngle, MIDDLE_ANGLE);
      mGapAngle = typedArray.getFloat(R.styleable.RoundProgress_gapAngle, GAP_ANGLE);
      float fullProgressAngle =
          typedArray.getFloat(R.styleable.RoundProgress_fullProgressAngle, FULLPROGRESS_ANGLE);
      if (fullProgressAngle > 0) {
        mFullProgressAngle = fullProgressAngle;
      }
    }

    final float density = getResources().getDisplayMetrics().density;
    mStrokeSize = STROKE_SIZE * density;

    mPaint.setAntiAlias(true);
    mPaint.setStyle(Paint.Style.STROKE);
    mPaint.setStrokeWidth(mStrokeSize);
    mPaint.setStrokeCap(Paint.Cap.ROUND);

    //TypedValue outValue = new TypedValue();
    //context.getTheme().resolveAttribute(R.attr.colorAccent, outValue, true);
    //mForegroundColor = outValue.data;
    //context.getTheme().resolveAttribute(R.attr.colorControlHighlight, outValue, true);
    //mBackgroundColor = outValue.data;

    //mProgress = 0;
    //mMax = 100;
  }

  public int getProgress() {
    return mProgress;
  }

  public void setProgress(int progress) {

    if (progress < 0) {
      progress = 0;
    }

    if (progress > mMax) {
      progress = mMax;
    }

    if (progress != mProgress) {
      mProgress = progress;
      invalidate();
    }
  }

  public int getMax() {
    return mMax;
  }

  public void setMax(int max) {
    if (max < 0) {
      max = 0;
    }
    if (max != mMax) {
      mMax = max;
      if (mProgress > max) {
        mProgress = max;
      }
      invalidate();
    }
  }

  public float getMorph() {
    return mMorph;
  }

  /**
   * Set the current morph value (from 0 to 1) used to transformation (from line to arc) and
   * vice-versa
   *
   * @param morph The morph value
   */
  public void setMorph(float morph) {
    if (morph != mMorph) {
      mMorph = morph;
      invalidate();
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    int width = Math.max(getSuggestedMinimumWidth(), (int) mStrokeSize);
    int height = Math.max(getSuggestedMinimumHeight(), (int) mStrokeSize);

    width += getPaddingLeft() + getPaddingRight();
    height += getPaddingTop() + getPaddingBottom();

    final int measuredWidth = resolveSizeAndState(width, widthMeasureSpec, 0);
    final int measuredHeight = resolveSizeAndState(height, heightMeasureSpec, 0);
    setMeasuredDimension(measuredWidth, measuredHeight);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {

    // Extra padding to avoid cuttings on arc.
    float xpad = mStrokeSize / 2f;

    mBounds.top = getPaddingTop() + xpad;
    mBounds.bottom = h - getPaddingBottom() - xpad;
    mBounds.left = getPaddingLeft() + xpad;
    mBounds.right = w - getPaddingRight() - xpad;
  }

  @Override
  protected void onDraw(Canvas canvas) {

    final float scale = mMax > 0 ? mProgress / (float) mMax : 0;
    float startAngle = mMiddleAngle - mStartAngle * mMorph;
    float sweepAngle = mFullProgressAngle * mMorph;

    if (mBounds.height() <= mStrokeSize) {

      // Draw the line

      mPaint.setColor(mBackgroundColor);
      canvas.drawLine(mBounds.left, mBounds.top, mBounds.right, mBounds.top, mPaint);

      float stopX = mBounds.width() * scale + mBounds.left;

      mPaint.setColor(mForegroundColor);
      canvas.drawLine(mBounds.left, mBounds.top, stopX, mBounds.top, mPaint);
    } else if (startAngle < 180f) {

      // Draw the arc

      mPaint.setColor(mBackgroundColor);
      canvas.drawArc(mBounds, startAngle, sweepAngle, false, mPaint);

      mPaint.setColor(mForegroundColor);
      canvas.drawArc(mBounds, startAngle, sweepAngle * scale, false, mPaint);
    } else {

      // Draw the semi-arc

      mPaint.setColor(mBackgroundColor);
      canvas.drawArc(mBounds, 180f, 180f, false, mPaint);

      mPaint.setColor(mForegroundColor);
      canvas.drawArc(mBounds, 180f, 180f * scale, false, mPaint);
    }
  }

  @Override
  public Parcelable onSaveInstanceState() {
    // Force our ancestor class to save its state
    Parcelable superState = super.onSaveInstanceState();
    SavedState ss = new SavedState(superState);

    ss.progress = mProgress;

    return ss;
  }

  @Override
  public void onRestoreInstanceState(Parcelable state) {
    SavedState ss = (SavedState) state;
    super.onRestoreInstanceState(ss.getSuperState());

    setProgress(ss.progress);
  }

  private static class SavedState extends BaseSavedState {

    public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
      public SavedState createFromParcel(Parcel in) {
        return new SavedState(in);
      }

      public SavedState[] newArray(int size) {
        return new SavedState[size];
      }
    };
    int progress;

    /**
     * Constructor called from {@link ProgressBar#onSaveInstanceState()}
     */
    SavedState(Parcelable superState) {
      super(superState);
    }

    /**
     * Constructor called from {@link #CREATOR}
     */
    private SavedState(Parcel in) {
      super(in);
      progress = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
      super.writeToParcel(out, flags);
      out.writeInt(progress);
    }
  }
}

package com.fanji.android.img.view.indicator;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.viewpager.widget.ViewPager;

import java.util.Locale;

/**
 * created by jiangshide on 2020/4/20.
 * email:18311271399@163.com
 */
public class NumberIndicator extends androidx.appcompat.widget.AppCompatTextView {
  private static final String STR_NUM_FORMAT = "%s/%s";

  private ViewPager mViewPager;
  private final ViewPager.OnPageChangeListener mInternalPageChangeListener =
      new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {

          if (mViewPager.getAdapter() == null || mViewPager.getAdapter().getCount() <= 0) {
            return;
          }

          setText(String.format(Locale.getDefault(),
              STR_NUM_FORMAT,
              position + 1,
              mViewPager.getAdapter().getCount()));
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
      };

  public NumberIndicator(Context context) {
    this(context, null);
  }

  public NumberIndicator(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public NumberIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initNumberIndicator();
  }

  private void initNumberIndicator() {
    setTextColor(Color.WHITE);
    setTextSize(18);
  }

  public void setViewPager(ViewPager viewPager) {
    if (viewPager != null && viewPager.getAdapter() != null) {
      mViewPager = viewPager;
      mViewPager.removeOnPageChangeListener(mInternalPageChangeListener);
      mViewPager.addOnPageChangeListener(mInternalPageChangeListener);
      mInternalPageChangeListener.onPageSelected(mViewPager.getCurrentItem());
    }
  }
}

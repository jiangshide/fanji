package com.fanji.android.ui.tablayout.titles;

import android.content.Context;

import com.fanji.android.ui.tablayout.adapter.ArgbEvaluatorHolder;

/**
 * created by jiangshide on 2019-08-05.
 * email:18311271399@163.com
 */
public class ColorTransitionPagerTitleView extends SimplePagerTitleView {
  public ColorTransitionPagerTitleView(Context context) {
    super(context);
  }

  @Override
  public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
    int color = ArgbEvaluatorHolder.eval(leavePercent, mSelectedColor, mNormalColor);
    setTextColor(color);
    if(mNormalSize == mSelectedSize){
      setTextSize(mNormalSize);
    }else{
      setTextSize(mNormalSize + (mSelectedSize - mNormalSize) * (1 - leavePercent));
    }
  }

  @Override
  public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
    int color = ArgbEvaluatorHolder.eval(enterPercent, mNormalColor, mSelectedColor);
    setTextColor(color);
    if(mNormalSize == mSelectedSize){
      setTextSize(mNormalSize);
    }else{
      setTextSize(mNormalSize + (mSelectedSize - mNormalSize) * enterPercent);
    }
  }

  @Override
  public void onSelected(int index, int totalCount) {
  }

  @Override
  public void onDeselected(int index, int totalCount) {
  }
}

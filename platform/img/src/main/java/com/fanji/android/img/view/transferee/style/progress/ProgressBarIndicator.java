package com.fanji.android.img.view.transferee.style.progress;

import android.content.Context;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.fanji.android.img.view.transferee.style.IProgressIndicator;

/**
 * created by jiangshide on 2020/4/20.
 * email:18311271399@163.com
 */
public class ProgressBarIndicator implements IProgressIndicator {
  private SparseArray<ProgressBar> progressBarArray = new SparseArray<>();

  private int dip2Px(Context context, float dpValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dpValue * scale + 0.5f);
  }

  @Override
  public void attach(int position, FrameLayout parent) {
    Context context = parent.getContext();

    int progressSize = dip2Px(context, 50);
    FrameLayout.LayoutParams progressLp = new FrameLayout.LayoutParams(
        progressSize, progressSize);
    progressLp.gravity = Gravity.CENTER;

    ProgressBar progressBar = new ProgressBar(context);
    progressBar.setLayoutParams(progressLp);

    parent.addView(progressBar, parent.getChildCount());
    progressBarArray.put(position, progressBar);
  }

  @Override
  public void hideView(int position) {
    ProgressBar progressBar = progressBarArray.get(position);
    if (progressBar != null)
      progressBar.setVisibility(View.GONE);
  }

  @Override
  public void onStart(int position) {
  }

  @Override
  public void onProgress(int position, int progress) {
  }

  @Override
  public void onFinish(int position) {
    ProgressBar progressBar = progressBarArray.get(position);
    if (progressBar == null) return;

    ViewGroup vg = (ViewGroup) progressBar.getParent();
    ;
    if (vg != null) {
      vg.removeView(progressBar);
    }
  }

}

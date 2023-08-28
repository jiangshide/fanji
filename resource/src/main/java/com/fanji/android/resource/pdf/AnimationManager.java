package com.fanji.android.resource.pdf;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.PointF;
import android.view.animation.DecelerateInterpolator;

/**
 * @author: jiangshide
 * @date: 2023/8/20
 * @email: 18311271399@163.com
 * @description:
 */
class AnimationManager {

    /** PDF View */
    private PDFView pdfView;

    private ValueAnimator animation;

    public AnimationManager(PDFView pdfView) {
        this.pdfView = pdfView;
    }

    public void startXAnimation(float xFrom, float xTo) {
        if (animation != null) {
            animation.cancel();
        }
        animation = ValueAnimator.ofFloat(xFrom, xTo);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.addUpdateListener(new XAnimation());
        animation.setDuration(400);
        animation.start();
    }

    public void startYAnimation(float yFrom, float yTo) {
        if (animation != null) {
            animation.cancel();
        }
        animation = ValueAnimator.ofFloat(yFrom, yTo);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.addUpdateListener(new YAnimation());
        animation.setDuration(400);
        animation.start();
    }

    public void startZoomAnimation(float zoomFrom, float zoomTo) {
        if (animation != null) {
            animation.cancel();
        }
        animation = ValueAnimator.ofFloat(zoomFrom, zoomTo);
        animation.setInterpolator(new DecelerateInterpolator());
        ZoomAnimation zoomAnim = new ZoomAnimation();
        animation.addUpdateListener(zoomAnim);
        animation.addListener(zoomAnim);
        animation.setDuration(400);
        animation.start();
    }

    public void stopAll() {
        if (animation != null) {
            animation.cancel();
            animation = null;
        }
    }

    class XAnimation implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float offset = (Float) animation.getAnimatedValue();
            pdfView.moveTo(offset, pdfView.getCurrentYOffset());
        }

    }

    class YAnimation implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float offset = (Float) animation.getAnimatedValue();
            pdfView.moveTo(pdfView.getCurrentXOffset(), offset);
        }

    }

    class ZoomAnimation implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float zoom = (Float) animation.getAnimatedValue();
            pdfView.zoomCenteredTo(zoom, new PointF(pdfView.getWidth() / 2, pdfView.getHeight() / 2));
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            pdfView.loadPages();
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }

        @Override
        public void onAnimationStart(Animator animation) {
        }

    }

}

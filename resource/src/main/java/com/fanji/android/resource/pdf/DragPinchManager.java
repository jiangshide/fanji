package com.fanji.android.resource.pdf;

import static com.fanji.android.resource.pdf.util.Constants.Pinch.MAXIMUM_ZOOM;
import static com.fanji.android.resource.pdf.util.Constants.Pinch.MINIMUM_ZOOM;
import static com.fanji.android.resource.pdf.util.Constants.Pinch.QUICK_MOVE_THRESHOLD_DISTANCE;
import static com.fanji.android.resource.pdf.util.Constants.Pinch.QUICK_MOVE_THRESHOLD_TIME;

import android.graphics.PointF;

import com.fanji.android.resource.pdf.util.DragPinchListener;

/**
 * @author: jiangshide
 * @date: 2023/8/20
 * @email: 18311271399@163.com
 * @description:
 */
class DragPinchManager implements DragPinchListener.OnDragListener, DragPinchListener.OnPinchListener, DragPinchListener.OnDoubleTapListener {

    private PDFView pdfView;

    private DragPinchListener dragPinchListener;

    private long startDragTime;

    private float startDragX;
    private float startDragY;

    private boolean isSwipeEnabled;

    private boolean swipeVertical;

    public DragPinchManager(PDFView pdfView) {
        this.pdfView = pdfView;
        this.isSwipeEnabled = false;
        this.swipeVertical = pdfView.isSwipeVertical();
        dragPinchListener = new DragPinchListener();
        dragPinchListener.setOnDragListener(this);
        dragPinchListener.setOnPinchListener(this);
        dragPinchListener.setOnDoubleTapListener(this);
        pdfView.setOnTouchListener(dragPinchListener);
    }

    public void enableDoubletap(boolean enableDoubletap){
        if (enableDoubletap) {
            dragPinchListener.setOnDoubleTapListener(this);
        } else {
            dragPinchListener.setOnDoubleTapListener(null);
        }
    }

    @Override
    public void onPinch(float dr, PointF pivot) {
        float wantedZoom = pdfView.getZoom() * dr;
        if (wantedZoom < MINIMUM_ZOOM) {
            dr = MINIMUM_ZOOM / pdfView.getZoom();
        } else if (wantedZoom > MAXIMUM_ZOOM) {
            dr = MAXIMUM_ZOOM / pdfView.getZoom();
        }
        pdfView.zoomCenteredRelativeTo(dr, pivot);
    }

    @Override
    public void startDrag(float x, float y) {
        startDragTime = System.currentTimeMillis();
        startDragX = x;
        startDragY = y;
    }

    @Override
    public void onDrag(float dx, float dy) {
        if (isZooming() || isSwipeEnabled) {
            pdfView.moveRelativeTo(dx, dy);
        }
    }

    @Override
    public void endDrag(float x, float y) {
        if (!isZooming()) {
            if (isSwipeEnabled) {
                float distance;
                if (swipeVertical)
                    distance = y - startDragY;
                else
                    distance = x - startDragX;

                long time = System.currentTimeMillis() - startDragTime;
                int diff = distance > 0 ? -1 : +1;

                if (isQuickMove(distance, time) || isPageChange(distance)) {
                    pdfView.showPage(pdfView.getCurrentPage() + diff);
                } else {
                    pdfView.showPage(pdfView.getCurrentPage());
                }
            }
        } else {
            pdfView.loadPages();
        }
    }

    public boolean isZooming() {
        return pdfView.isZooming();
    }

    private boolean isPageChange(float distance) {
        return Math.abs(distance) > Math.abs(pdfView.toCurrentScale(pdfView.getOptimalPageWidth()) / 2);
    }

    private boolean isQuickMove(float dx, long dt) {
        return Math.abs(dx) >= QUICK_MOVE_THRESHOLD_DISTANCE && //
                dt <= QUICK_MOVE_THRESHOLD_TIME;
    }

    public void setSwipeEnabled(boolean isSwipeEnabled) {
        this.isSwipeEnabled = isSwipeEnabled;
    }

    @Override
    public void onDoubleTap(float x, float y) {
        if (isZooming()) {
            pdfView.resetZoomWithAnimation();
        }
    }

    public void setSwipeVertical(boolean swipeVertical) {
        this.swipeVertical = swipeVertical;
    }

}

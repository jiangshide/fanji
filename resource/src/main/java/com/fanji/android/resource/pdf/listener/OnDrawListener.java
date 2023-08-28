package com.fanji.android.resource.pdf.listener;

import android.graphics.Canvas;

/**
 * @author: jiangshide
 * @date: 2023/8/20
 * @email: 18311271399@163.com
 * @description:
 */
public interface OnDrawListener {

    /**
     * This method is called when the PDFView is
     * drawing its view.
     * <p/>
     * The page is starting at (0,0)
     * @param canvas        The canvas on which to draw things.
     * @param pageWidth     The width of the current page.
     * @param pageHeight    The height of the current page.
     * @param displayedPage The current page index
     */
    void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage);
}

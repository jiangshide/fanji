package com.fanji.android.resource.pdf.listener;

/**
 * @author: jiangshide
 * @date: 2023/8/20
 * @email: 18311271399@163.com
 * @description:
 */
public interface OnPageChangeListener {

    /**
     * Called when the user use swipe to change page
     * @param page      the new page displayed, starting from 1
     * @param pageCount the total page count, starting from 1
     */
    void onPageChanged(int page, int pageCount);

}

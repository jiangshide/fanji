package com.fanji.android.ui.refresh.impl;

import android.annotation.SuppressLint;
import android.view.View;

import com.fanji.android.ui.refresh.api.RefreshFooter;
import com.fanji.android.ui.refresh.internal.InternalAbstract;

/**
 * created by jiangshide on 2016-07-24.
 * email:18311271399@163.com
 */
@SuppressLint("ViewConstructor")
public class RefreshFooterWrapper extends InternalAbstract
    implements RefreshFooter/*, InvocationHandler */{

    public RefreshFooterWrapper(View wrapper) {
        super(wrapper);
    }

//    @Override
//    public boolean setNoMoreData(boolean noMoreData) {
//        return mWrappedInternal instanceof RefreshFooter && ((RefreshFooter) mWrappedInternal).setNoMoreData(noMoreData);
//    }

}

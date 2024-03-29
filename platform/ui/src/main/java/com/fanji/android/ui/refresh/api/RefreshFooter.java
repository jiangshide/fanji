package com.fanji.android.ui.refresh.api;


import androidx.annotation.RestrictTo;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;
import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static androidx.annotation.RestrictTo.Scope.SUBCLASSES;

/**
 * created by jiangshide on 2016-07-24.
 * email:18311271399@163.com
 */
public interface RefreshFooter extends RefreshInternal {

    /**
     * 【仅限框架内调用】设置数据全部加载完成，将不能再次触发加载功能
     * @param noMoreData 是否有更多数据
     * @return true 支持全部加载完成的状态显示 false 不支持
     */
    @RestrictTo({LIBRARY,LIBRARY_GROUP,SUBCLASSES})
    boolean setNoMoreData(boolean noMoreData);
}

package com.fanji.android.ui.navigation.listener;

/**
 * created by jiangshide on 4/10/21.
 * email:18311271399@163.com
 */
public interface SimpleTabItemSelectedListener {

    /**
     * 选中导航栏的某一项
     *
     * @param index 索引导航按钮，按添加顺序排序
     * @param old   前一个选中项，如果没有就等于-1
     */
    void onSelected(int index, int old);

}

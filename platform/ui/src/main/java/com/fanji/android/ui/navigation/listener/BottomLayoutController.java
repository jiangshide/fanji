package com.fanji.android.ui.navigation.listener;

import androidx.viewpager.widget.ViewPager;

/**
 * created by jiangshide on 4/10/21.
 * email:18311271399@163.com
 */
public interface BottomLayoutController {

    /**
     * 方便适配ViewPager页面切换<p>
     * 注意：ViewPager页面数量必须等于导航栏的Item数量
     *
     * @param viewPager {@link ViewPager}
     */
    void setupWithViewPager(ViewPager viewPager);

    /**
     * 向下移动隐藏导航栏
     */
    void hideBottomLayout();

    /**
     * 向上移动显示导航栏
     */
    void showBottomLayout();
}
